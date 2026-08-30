package com.training.module2_workflow.controller;

import com.training.common.entity.Application;
import com.training.common.entity.VerificationLog;
import com.training.module2_workflow.dto.ApplicationSubmissionDTO;
import com.training.module2_workflow.dto.VerificationDecisionDTO;
import com.training.module2_workflow.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('BENEFICIARY')")
    public ResponseEntity<Application> submitApplication(@Valid @RequestBody ApplicationSubmissionDTO dto) {
        Application application = applicationService.submitApplication(dto);
        return new ResponseEntity<>(application, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Application> getApplicationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @Autowired
    private com.training.module2_workflow.repository.ApplicationRepository applicationRepository;

    @GetMapping("/queue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<?> getQueueByStatus(
            @RequestParam("status") com.training.common.enums.AppStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(applicationRepository.findByStatus(status, org.springframework.data.domain.PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(applicationService.getQueueByStatus(status));
    }

    @PostMapping("/{id}/decide")
    @PreAuthorize("hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<Application> transitionStatus(@PathVariable("id") Long id, @Valid @RequestBody VerificationDecisionDTO dto) {
        Application application = applicationService.transitionStatus(id, dto);
        return ResponseEntity.ok(application);
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('BENEFICIARY') or hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<List<VerificationLog>> getVerificationHistory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(applicationService.getVerificationHistory(id));
    }

    @GetMapping("/officer/{officerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FIELD_OFFICER') or hasRole('DISTRICT_OFFICER') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<?> getApplicationsByOfficer(
            @PathVariable("officerId") Long officerId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(applicationRepository.findByAssignedOfficerId(officerId, org.springframework.data.domain.PageRequest.of(page, size)));
        }
        return ResponseEntity.ok(applicationRepository.findByAssignedOfficerId(officerId));
    }
}
