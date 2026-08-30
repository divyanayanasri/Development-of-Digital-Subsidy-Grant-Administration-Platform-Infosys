package com.training.module2_workflow.service.impl;

import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.common.dto.SchemeCriteriaDTO;
import com.training.common.entity.Application;
import com.training.common.entity.VerificationLog;
import com.training.common.enums.AppStatus;
import com.training.common.enums.RouteType;
import com.training.common.exception.ResourceNotFoundException;
import com.training.module1_masterdata.service.BeneficiaryService;
import com.training.module1_masterdata.service.SchemeService;
import com.training.module2_workflow.dto.ApplicationSubmissionDTO;
import com.training.module2_workflow.dto.VerificationDecisionDTO;
import com.training.module2_workflow.event.ApplicationApprovedEvent;
import com.training.module2_workflow.repository.ApplicationRepository;
import com.training.module2_workflow.repository.VerificationLogRepository;
import com.training.module2_workflow.service.ApplicationService;
import com.training.module2_workflow.service.ScoringEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private static final int FAST_TRACK_MIN_SCORE = 70;

    // "grant amount below threshold" -> the scheme's max grant amount compared
    // against this configurable ceiling. Below it, and paired with a high
    // enough score, the application can skip district review.
    @Value("${workflow.fast-track.grant-threshold:50000}")
    private BigDecimal fastTrackGrantThreshold;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private ScoringEngine scoringEngine;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private VerificationLogRepository verificationLogRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private com.training.common.service.AuditLogService auditLogService;

    /**
     * Valid next statuses for the ESCALATED route (full four-stage review).
     */
    private static final Map<AppStatus, Set<AppStatus>> ESCALATED_TRANSITIONS = new EnumMap<>(AppStatus.class);
    /**
     * Valid next statuses for the FAST_TRACK route (skips DISTRICT_REVIEW).
     */
    private static final Map<AppStatus, Set<AppStatus>> FAST_TRACK_TRANSITIONS = new EnumMap<>(AppStatus.class);

    static {
        ESCALATED_TRANSITIONS.put(AppStatus.SUBMITTED, EnumSet.of(AppStatus.FIELD_REVIEW, AppStatus.REJECTED));
        ESCALATED_TRANSITIONS.put(AppStatus.FIELD_REVIEW, EnumSet.of(AppStatus.DISTRICT_REVIEW, AppStatus.RE_VERIFICATION, AppStatus.REJECTED));
        ESCALATED_TRANSITIONS.put(AppStatus.DISTRICT_REVIEW, EnumSet.of(AppStatus.FINANCE_REVIEW, AppStatus.RE_VERIFICATION, AppStatus.REJECTED));
        ESCALATED_TRANSITIONS.put(AppStatus.FINANCE_REVIEW, EnumSet.of(AppStatus.APPROVED, AppStatus.RE_VERIFICATION, AppStatus.REJECTED));
        ESCALATED_TRANSITIONS.put(AppStatus.RE_VERIFICATION, EnumSet.of(AppStatus.FIELD_REVIEW));
        ESCALATED_TRANSITIONS.put(AppStatus.APPROVED, EnumSet.noneOf(AppStatus.class));
        ESCALATED_TRANSITIONS.put(AppStatus.REJECTED, EnumSet.noneOf(AppStatus.class));

        FAST_TRACK_TRANSITIONS.put(AppStatus.SUBMITTED, EnumSet.of(AppStatus.FIELD_REVIEW, AppStatus.REJECTED));
        FAST_TRACK_TRANSITIONS.put(AppStatus.FIELD_REVIEW, EnumSet.of(AppStatus.FINANCE_REVIEW, AppStatus.RE_VERIFICATION, AppStatus.REJECTED));
        FAST_TRACK_TRANSITIONS.put(AppStatus.FINANCE_REVIEW, EnumSet.of(AppStatus.APPROVED, AppStatus.RE_VERIFICATION, AppStatus.REJECTED));
        FAST_TRACK_TRANSITIONS.put(AppStatus.RE_VERIFICATION, EnumSet.of(AppStatus.FIELD_REVIEW));
        FAST_TRACK_TRANSITIONS.put(AppStatus.APPROVED, EnumSet.noneOf(AppStatus.class));
        FAST_TRACK_TRANSITIONS.put(AppStatus.REJECTED, EnumSet.noneOf(AppStatus.class));
        // DISTRICT_REVIEW is deliberately absent for FAST_TRACK - it is skipped.
    }

    @Override
    public Application submitApplication(ApplicationSubmissionDTO dto) {
        // Depends only on the Module 1 common/dto contract. If Module 1 isn't
        // wired up yet, swap these two calls for hardcoded test DTOs - the
        // rest of this method (and ScoringEngine) needs no changes either way.
        BeneficiaryEligibilityDTO eligibility = beneficiaryService.getBeneficiaryEligibility(dto.getBeneficiaryId());
        SchemeCriteriaDTO criteria = schemeService.getSchemeCriteria(dto.getSchemeId());

        int score = scoringEngine.calculateScore(eligibility, criteria);
        RouteType routeType = determineRoute(score, criteria);

        Application application = Application.builder()
                .beneficiaryId(dto.getBeneficiaryId())
                .schemeId(dto.getSchemeId())
                .eligibilityScore(score)
                .status(AppStatus.SUBMITTED)
                .routeType(routeType)
                .submittedAt(LocalDateTime.now())
                .build();

        Application saved = applicationRepository.save(application);
        auditLogService.log("SUBMIT_APPLICATION", "Application", saved.getId(), "Submitted application for beneficiary: " + saved.getBeneficiaryId() + " and scheme: " + saved.getSchemeId());
        return saved;
    }

    private RouteType determineRoute(int score, SchemeCriteriaDTO criteria) {
        BigDecimal grantAmount = criteria.getGrantAmountMax();
        boolean belowThreshold = grantAmount != null && grantAmount.compareTo(fastTrackGrantThreshold) < 0;
        return (score >= FAST_TRACK_MIN_SCORE && belowThreshold) ? RouteType.FAST_TRACK : RouteType.ESCALATED;
    }

    @Override
    @Transactional(readOnly = true)
    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getQueueByStatus(AppStatus status) {
        return applicationRepository.findByStatus(status);
    }

    @Override
    public Application transitionStatus(Long applicationId, VerificationDecisionDTO decisionDto) {
        Application application = getApplicationById(applicationId);
        AppStatus current = application.getStatus();
        AppStatus target = decisionDto.getTargetStatus();

        Map<AppStatus, Set<AppStatus>> transitions =
                application.getRouteType() == RouteType.FAST_TRACK ? FAST_TRACK_TRANSITIONS : ESCALATED_TRANSITIONS;

        Set<AppStatus> allowedNext = transitions.getOrDefault(current, EnumSet.noneOf(AppStatus.class));
        if (!allowedNext.contains(target)) {
            throw new IllegalArgumentException(
                    "Invalid transition from " + current + " to " + target + " for route " + application.getRouteType());
        }

        application.setStatus(target);
        application.setAssignedOfficerId(decisionDto.getOfficerId());
        if (target == AppStatus.APPROVED || target == AppStatus.REJECTED) {
            application.setDecidedAt(LocalDateTime.now());
        }
        Application saved = applicationRepository.save(application);

        VerificationLog log = VerificationLog.builder()
                .applicationId(saved.getId())
                .officerId(decisionDto.getOfficerId())
                .role(decisionDto.getRole())
                .decision(decisionDto.getDecision())
                .remarks(decisionDto.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();
        verificationLogRepository.save(log);

        auditLogService.log("TRANSITION_APPLICATION", "Application", saved.getId(), "Transition application status from " + current + " to " + target + " by Officer: " + decisionDto.getOfficerId());

        if (target == AppStatus.APPROVED) {
            SchemeCriteriaDTO criteria = schemeService.getSchemeCriteria(saved.getSchemeId());
            eventPublisher.publishEvent(new ApplicationApprovedEvent(
                    this,
                    saved.getId(),
                    saved.getBeneficiaryId(),
                    saved.getSchemeId(),
                    criteria.getGrantAmountMax(),
                    saved.getDecidedAt()
            ));
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationLog> getVerificationHistory(Long applicationId) {
        getApplicationById(applicationId); // 404s if missing
        return verificationLogRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId);
    }
}
