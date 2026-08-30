package com.training.module3_disbursement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.common.entity.*;
import com.training.common.enums.*;
import com.training.module1_masterdata.repository.*;
import com.training.module2_workflow.repository.ApplicationRepository;
import com.training.module3_disbursement.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementPlanRepository planRepository;
    private final DisbursementStageRepository stageRepository;
    private final ComplianceFlagRepository complianceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private com.training.common.service.AuditLogService auditLogService;

    @Autowired
    public DisbursementServiceImpl(
            DisbursementPlanRepository planRepository,
            DisbursementStageRepository stageRepository,
            ComplianceFlagRepository complianceRepository) {
        this.planRepository = planRepository;
        this.stageRepository = stageRepository;
        this.complianceRepository = complianceRepository;
    }

    @Override
    public void createDisbursementPlan(Long applicationId) {
        // Prevent duplicate plan creation
        if (planRepository.findByApplicationId(applicationId).isPresent()) {
            return;
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));

        Scheme scheme = schemeRepository.findById(application.getSchemeId())
                .orElseThrow(() -> new IllegalArgumentException("Scheme not found with ID: " + application.getSchemeId()));

        BigDecimal totalAmount = scheme.getGrantAmountMax() != null ? scheme.getGrantAmountMax() : new BigDecimal("50000.00");

        // Save plan
        DisbursementPlan plan = DisbursementPlan.builder()
                .applicationId(applicationId)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();
        plan = planRepository.save(plan);

        // Generate 4 stages
        // Stage 1: 25% (Documentation Complete) - Released immediately
        BigDecimal s1Amt = totalAmount.multiply(new BigDecimal("0.25"));
        DisbursementStage stage1 = DisbursementStage.builder()
                .planId(plan.getId())
                .stageNo(1)
                .milestoneName("Documentation Complete")
                .percentage(25.0)
                .amount(s1Amt)
                .dueDate(LocalDate.now().plusDays(2))
                .status(StageStatus.RELEASED)
                .releasedAt(LocalDateTime.now())
                .build();
        stageRepository.save(stage1);

        // Update Region's budgetUsed
        updateRegionBudget(application.getBeneficiaryId(), s1Amt);

        // Audit Log
        auditLogService.log("CREATE_DISBURSEMENT_PLAN", "DisbursementPlan", plan.getId(), "Created disbursement plan for application ID: " + applicationId);
        auditLogService.log("RELEASE_DISBURSEMENT_STAGE", "DisbursementStage", stage1.getId(), "Automatically released Stage 1 for plan ID: " + plan.getId());

        // Stage 2: 35% (Ground Verification) - Pending
        stageRepository.save(DisbursementStage.builder()
                .planId(plan.getId())
                .stageNo(2)
                .milestoneName("Ground Verification")
                .percentage(35.0)
                .amount(totalAmount.multiply(new BigDecimal("0.35")))
                .dueDate(LocalDate.now().plusDays(15))
                .status(StageStatus.PENDING)
                .build());

        // Stage 3: 30% (Utilization Proof) - Pending
        stageRepository.save(DisbursementStage.builder()
                .planId(plan.getId())
                .stageNo(3)
                .milestoneName("Utilization Proof")
                .percentage(30.0)
                .amount(totalAmount.multiply(new BigDecimal("0.30")))
                .dueDate(LocalDate.now().plusDays(30))
                .status(StageStatus.PENDING)
                .build());

        // Stage 4: 10% (Project Closure) - Pending
        stageRepository.save(DisbursementStage.builder()
                .planId(plan.getId())
                .stageNo(4)
                .milestoneName("Project Closure")
                .percentage(10.0)
                .amount(totalAmount.multiply(new BigDecimal("0.10")))
                .dueDate(LocalDate.now().plusDays(45))
                .status(StageStatus.PENDING)
                .build());
    }

    private void updateRegionBudget(Long beneficiaryId, BigDecimal amount) {
        try {
            Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId).orElse(null);
            if (beneficiary != null) {
                User user = userRepository.findById(beneficiary.getUserId()).orElse(null);
                if (user != null && user.getRegionId() != null) {
                    Region region = regionRepository.findById(user.getRegionId()).orElse(null);
                    if (region != null) {
                        BigDecimal used = region.getBudgetUsed() != null ? region.getBudgetUsed() : BigDecimal.ZERO;
                        region.setBudgetUsed(used.add(amount));
                        regionRepository.save(region);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating region budget: " + e.getMessage());
        }
    }

    @Override
    public void configureDisbursementPlan(com.training.module3_disbursement.dto.DisbursementConfigurationDTO configDto) {
        if (configDto.getStages() == null || configDto.getStages().isEmpty()) {
            throw new IllegalArgumentException("Stages list cannot be empty");
        }

        // Validate that the percentages sum up to exactly 100.0
        double totalPercentage = configDto.getStages().stream()
                .mapToDouble(com.training.module3_disbursement.dto.DisbursementConfigurationDTO.StageConfigDTO::getPercentage)
                .sum();
        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException("Total stages percentage must sum to exactly 100.0%. Found: " + totalPercentage + "%");
        }

        Long applicationId = configDto.getApplicationId();
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));

        Scheme scheme = schemeRepository.findById(application.getSchemeId())
                .orElseThrow(() -> new IllegalArgumentException("Scheme not found with ID: " + application.getSchemeId()));

        BigDecimal totalAmount = scheme.getGrantAmountMax() != null ? scheme.getGrantAmountMax() : new BigDecimal("50000.00");

        // Find or create DisbursementPlan
        DisbursementPlan plan = planRepository.findByApplicationId(applicationId)
                .orElseGet(() -> {
                    DisbursementPlan newPlan = DisbursementPlan.builder()
                            .applicationId(applicationId)
                            .totalAmount(totalAmount)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return planRepository.save(newPlan);
                });

        // Delete existing stages for this plan
        List<DisbursementStage> existingStages = stageRepository.findByPlanId(plan.getId());
        stageRepository.deleteAll(existingStages);

        // Save new custom stages
        for (com.training.module3_disbursement.dto.DisbursementConfigurationDTO.StageConfigDTO stageConfig : configDto.getStages()) {
            BigDecimal stageAmt = totalAmount.multiply(BigDecimal.valueOf(stageConfig.getPercentage() / 100.0));
            DisbursementStage stage = DisbursementStage.builder()
                    .planId(plan.getId())
                    .stageNo(stageConfig.getStageNo())
                    .milestoneName(stageConfig.getMilestoneName())
                    .percentage(stageConfig.getPercentage())
                    .amount(stageAmt)
                    .dueDate(stageConfig.getDueDate())
                    .status(stageConfig.getStageNo() == 1 ? StageStatus.RELEASED : StageStatus.PENDING)
                    .releasedAt(stageConfig.getStageNo() == 1 ? LocalDateTime.now() : null)
                    .build();
            stageRepository.save(stage);

            if (stageConfig.getStageNo() == 1) {
                // Update region budget for stage 1
                updateRegionBudget(application.getBeneficiaryId(), stageAmt);
            }
        }

        auditLogService.log("CONFIGURE_DISBURSEMENT_PLAN", "DisbursementPlan", plan.getId(), 
                "Configured custom disbursement plan with " + configDto.getStages().size() + " stages for application ID: " + applicationId);
    }

    @Override
    public void releaseStage(Long stageId) {
        DisbursementStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Disbursement stage not found with ID: " + stageId));
        
        if (stage.getStatus() == StageStatus.RELEASED) {
            return;
        }

        DisbursementPlan plan = planRepository.findById(stage.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Disbursement plan not found for stage ID: " + stageId));

        // Enforce sequential integrity: Stage N can only be released if N-1 is already released
        if (stage.getStageNo() > 1) {
            List<DisbursementStage> allStages = stageRepository.findByPlanId(plan.getId());
            Optional<DisbursementStage> prevStageOpt = allStages.stream()
                    .filter(s -> s.getStageNo() == stage.getStageNo() - 1)
                    .findFirst();

            if (prevStageOpt.isPresent()) {
                DisbursementStage prevStage = prevStageOpt.get();
                if (prevStage.getStatus() != StageStatus.RELEASED) {
                    throw new IllegalArgumentException("Sequential integrity violation: Cannot release Stage " 
                            + stage.getStageNo() + " because Stage " + prevStage.getStageNo() + " is not released yet.");
                }
            }
        }

        stage.setStatus(StageStatus.RELEASED);
        stage.setReleasedAt(LocalDateTime.now());
        stageRepository.save(stage);

        // Update budget used in region
        Application application = applicationRepository.findById(plan.getApplicationId()).orElse(null);
        if (application != null) {
            updateRegionBudget(application.getBeneficiaryId(), stage.getAmount());
        }

        // If this stage was flagged for compliance, resolve it
        List<ComplianceFlag> flags = complianceRepository.findByApplicationId(plan.getApplicationId());
        for (ComplianceFlag flag : flags) {
            if (flag.getStageId() != null && flag.getStageId().equals(stageId)) {
                flag.setResolved(true);
                complianceRepository.save(flag);
            }
        }

        // Audit Log
        auditLogService.log("RELEASE_DISBURSEMENT_STAGE", "DisbursementStage", stage.getId(), 
                "Released disbursement stage ID: " + stageId + " for plan ID: " + plan.getId());
    }

    @Override
    public void checkCompliance() {
        LocalDate today = LocalDate.now();
        List<DisbursementStage> pendingStages = stageRepository.findByStatus(StageStatus.PENDING);
        for (DisbursementStage stage : pendingStages) {
            if (stage.getDueDate() != null) {
                if (stage.getDueDate().isBefore(today)) {
                    stage.setStatus(StageStatus.MISSED);
                    stageRepository.save(stage);

                    // Raise compliance flag
                    DisbursementPlan plan = planRepository.findById(stage.getPlanId()).orElse(null);
                    if (plan != null) {
                        ComplianceFlag flag = complianceRepository.save(ComplianceFlag.builder()
                                .applicationId(plan.getApplicationId())
                                .stageId(stage.getId())
                                .flagType("MISSED_DEADLINE")
                                .raisedAt(LocalDateTime.now())
                                .resolved(false)
                                .build());

                        // Audit Log
                        auditLogService.log("COMPLIANCE_VIOLATION", "ComplianceFlag", flag.getId(), 
                                "Raised compliance flag for missed deadline on stage ID: " + stage.getId() 
                                + " under application ID: " + plan.getApplicationId());
                    }
                } else if (stage.getDueDate().isBefore(today.plusDays(4))) {
                    // Send due-date reminder if within 3 days
                    DisbursementPlan plan = planRepository.findById(stage.getPlanId()).orElse(null);
                    if (plan != null) {
                        auditLogService.log("DUE_DATE_REMINDER", "DisbursementStage", stage.getId(), 
                                "Sent compliance reminder to beneficiary for stage: " + stage.getMilestoneName() 
                                + " (due on " + stage.getDueDate() + ") under application ID: " + plan.getApplicationId());
                    }
                }
            }
        }
    }
}