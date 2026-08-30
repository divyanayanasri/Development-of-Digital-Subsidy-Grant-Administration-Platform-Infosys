package com.training.module1_masterdata.controller;

import com.training.common.entity.*;
import com.training.common.enums.AppStatus;
import com.training.common.enums.Role;
import com.training.common.enums.RouteType;
import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.module1_masterdata.dto.BeneficiaryRegistrationDTO;
import com.training.module1_masterdata.repository.BeneficiaryRepository;
import com.training.module1_masterdata.repository.UserRepository;
import com.training.module1_masterdata.repository.SchemeRepository;
import com.training.module1_masterdata.repository.RegionRepository;
import com.training.module1_masterdata.service.BeneficiaryService;
import com.training.module1_masterdata.service.DocumentService;
import com.training.module2_workflow.repository.ApplicationRepository;
import com.training.module2_workflow.repository.VerificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/beneficiaries")
@Validated
public class BeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private VerificationLogRepository verificationLogRepository;

    @PostMapping
    public ResponseEntity<Beneficiary> registerBeneficiary(@Valid @RequestBody BeneficiaryRegistrationDTO dto) {
        Beneficiary beneficiary = beneficiaryService.registerBeneficiary(dto);
        return new ResponseEntity<>(beneficiary, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<?> getApplications(@PathVariable("id") String idStr) {
        Long id = parseId(idStr);
        List<Application> applications = applicationRepository.findByBeneficiaryId(id);
        if (applications.isEmpty() && !idStr.equalsIgnoreCase(String.valueOf(id))) {
            applications = applicationRepository.findAll();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Application app : applications) {
            result.add(enrichApplication(app));
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/applications")
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<?> submitApplication(@PathVariable("id") String idStr, @RequestBody Map<String, Object> body) {
        Long benId = parseId(idStr);

        Object schemeIdObj = body.get("schemeId");
        Long schemeId = parseId(schemeIdObj != null ? schemeIdObj.toString() : "1");

        Double landSize = body.get("declaredLandSize") != null ? Double.parseDouble(body.get("declaredLandSize").toString()) : 1.5;
        BigDecimal income = body.get("declaredIncome") != null ? new BigDecimal(body.get("declaredIncome").toString()) : new BigDecimal("100000.00");
        String category = body.get("declaredCategory") != null ? body.get("declaredCategory").toString() : "GENERAL";

        // Scoring algorithm
        int score = 100;
        Optional<Scheme> schemeOpt = schemeRepository.findById(schemeId);
        if (schemeOpt.isPresent()) {
            Scheme s = schemeOpt.get();
            if (s.getMaxIncome() != null && s.getMaxIncome().compareTo(BigDecimal.ZERO) > 0) {
                double ratio = income.doubleValue() / s.getMaxIncome().doubleValue();
                score -= (int) Math.min(45, Math.round(ratio * 40));
            }
            if (s.getMinLandSize() != null && s.getMinLandSize() > 0) {
                double ratio = landSize / s.getMinLandSize();
                score -= (int) Math.min(35, Math.round(ratio * 30));
            }
        }
        score = Math.max(15, Math.min(98, score));
        RouteType routeType = (score < 70 || landSize > 5.0) ? RouteType.ESCALATED : RouteType.FAST_TRACK;

        Application app = Application.builder()
                .beneficiaryId(benId)
                .schemeId(schemeId)
                .eligibilityScore(score)
                .status(AppStatus.SUBMITTED)
                .routeType(routeType)
                .submittedAt(LocalDateTime.now())
                .build();

        Application saved = applicationRepository.save(app);

        // Verification log
        VerificationLog vLog = VerificationLog.builder()
                .applicationId(saved.getId())
                .officerId(benId)
                .role(Role.BENEFICIARY)
                .decision("SUBMITTED")
                .remarks("Application submitted online. Score generated: " + score + "/100. Route assigned: " + routeType.name())
                .createdAt(LocalDateTime.now())
                .build();
        verificationLogRepository.save(vLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(enrichApplication(saved));
    }

    @GetMapping("/{id}/eligibility")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<BeneficiaryEligibilityDTO> getEligibilityDTO(@PathVariable("id") Long id) {
        BeneficiaryEligibilityDTO eligibility = beneficiaryService.getBeneficiaryEligibility(id);
        return ResponseEntity.ok(eligibility);
    }

    @GetMapping("/{id}/check-documents")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Boolean> checkRequiredDocuments(@PathVariable("id") Long id) {
        boolean allUploaded = documentService.checkRequiredDocuments(id);
        return ResponseEntity.ok(allUploaded);
    }

    private Long parseId(String input) {
        if (input == null) return 1L;
        String clean = input.replaceAll("[^0-9]", "");
        if (clean.isEmpty()) return 1L;
        try {
            return Long.parseLong(clean);
        } catch (Exception e) {
            return 1L;
        }
    }

    private Map<String, Object> enrichApplication(Application app) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", String.valueOf(app.getId()));
        dto.put("numericId", app.getId());
        dto.put("caseNumber", "GOV/2026/CENT/" + String.format("%05d", app.getId()));
        dto.put("eligibilityScore", app.getEligibilityScore() != null ? app.getEligibilityScore() : 85);
        dto.put("routeType", app.getRouteType() != null ? app.getRouteType().name() : "FAST_TRACK");
        dto.put("status", app.getStatus() != null ? app.getStatus().name() : "SUBMITTED");
        dto.put("appliedDate", app.getSubmittedAt() != null ? app.getSubmittedAt().toString() : LocalDateTime.now().toString());

        Optional<Beneficiary> benOpt = beneficiaryRepository.findById(app.getBeneficiaryId());
        if (benOpt.isPresent()) {
            Beneficiary ben = benOpt.get();
            dto.put("beneficiaryId", String.valueOf(ben.getId()));
            dto.put("declaredIncome", ben.getAnnualIncome());
            dto.put("declaredLandSize", ben.getLandSize());
            dto.put("declaredCategory", ben.getCategory());
            dto.put("beneficiaryAadhaar", ben.getAadharNo());

            Optional<User> userOpt = userRepository.findById(ben.getUserId());
            if (userOpt.isPresent()) {
                User u = userOpt.get();
                dto.put("beneficiaryName", u.getName());
                if (u.getRegionId() != null) {
                    Optional<Region> rOpt = regionRepository.findById(u.getRegionId());
                    rOpt.ifPresent(region -> dto.put("region", region.getName()));
                }
            }
        }
        if (!dto.containsKey("beneficiaryName")) dto.put("beneficiaryName", "Citizen Beneficiary");
        if (!dto.containsKey("region")) dto.put("region", "Central District");

        Optional<Scheme> schemeOpt = schemeRepository.findById(app.getSchemeId());
        if (schemeOpt.isPresent()) {
            dto.put("schemeName", schemeOpt.get().getName());
            dto.put("schemeId", String.valueOf(schemeOpt.get().getId()));
        } else {
            dto.put("schemeName", "Government Subsidy Grant");
            dto.put("schemeId", String.valueOf(app.getSchemeId()));
        }

        List<VerificationLog> history = verificationLogRepository.findByApplicationIdOrderByCreatedAtAsc(app.getId());
        List<Map<String, Object>> histList = new ArrayList<>();
        for (VerificationLog log : history) {
            Map<String, Object> hMap = new HashMap<>();
            hMap.put("id", "hist_" + log.getId());
            hMap.put("timestamp", log.getCreatedAt() != null ? log.getCreatedAt().toString() : LocalDateTime.now().toString());
            hMap.put("action", log.getDecision() != null ? log.getDecision() : "SUBMITTED");
            hMap.put("officerName", "System Officer");
            hMap.put("officerRole", log.getRole() != null ? log.getRole().name() : "FIELD_OFFICER");
            hMap.put("remarks", log.getRemarks());
            histList.add(hMap);
        }
        dto.put("verificationHistory", histList);

        Map<String, String> docs = new HashMap<>();
        docs.put("Aadhaar Card", "http://example.com/docs/aadhaar.pdf");
        docs.put("Income Certificate", "http://example.com/docs/income.pdf");
        dto.put("documentUrls", docs);

        return dto;
    }
}
