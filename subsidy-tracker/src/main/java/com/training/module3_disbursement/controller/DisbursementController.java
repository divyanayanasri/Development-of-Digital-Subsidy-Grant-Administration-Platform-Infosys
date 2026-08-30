package com.training.module3_disbursement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.training.common.entity.*;
import com.training.module3_disbursement.repository.*;
import com.training.module3_disbursement.service.DisbursementService;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.*;

@RestController
public class DisbursementController {

    @Autowired
    private DisbursementService service;

    @Autowired
    private DisbursementPlanRepository planRepository;

    @Autowired
    private DisbursementStageRepository stageRepository;

    @Autowired
    private ComplianceFlagRepository complianceFlagRepository;

    // Original Endpoints (Updated to use /api and return JSON)
    @PostMapping("/api/disbursement/create/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Map<String, String>> createPlan(@PathVariable Long applicationId) {
        service.createDisbursementPlan(applicationId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Disbursement Plan Created"));
    }

    @PostMapping("/api/disbursement/configure")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Map<String, String>> configurePlan(@RequestBody com.training.module3_disbursement.dto.DisbursementConfigurationDTO configDto) {
        service.configureDisbursementPlan(configDto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Disbursement Plan Configured Successfully"));
    }

    @PostMapping("/api/disbursement/release/{stageId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Map<String, String>> releaseStage(@PathVariable Long stageId) {
        service.releaseStage(stageId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Stage Released"));
    }

    // Module 3 Specification Endpoints
    @GetMapping("/api/applications/{id}/disbursement-plan")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER') or hasRole('BENEFICIARY')")
    public ResponseEntity<Object> getDisbursementPlanDetail(@PathVariable("id") Long applicationId) {
        Optional<DisbursementPlan> plan = planRepository.findByApplicationId(applicationId);
        if (plan.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<DisbursementStage> stages = stageRepository.findByPlanId(plan.get().getId());
        Map<String, Object> response = new HashMap<>();
        response.put("plan", plan.get());
        response.put("stages", stages);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/disbursement/{stageId}/complete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<String> completeStage(@PathVariable("stageId") Long stageId) {
        service.releaseStage(stageId);
        return ResponseEntity.ok("Stage Completed & Released Successfully");
    }

    @GetMapping("/api/disbursement/non-compliant")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER') or hasRole('DISTRICT_OFFICER')")
    public ResponseEntity<List<ComplianceFlag>> getNonCompliantFlags() {
        return ResponseEntity.ok(complianceFlagRepository.findByResolvedFalse());
    }
}