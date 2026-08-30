package com.training.module4_analytics.controller;

import com.training.module4_analytics.dto.*;
import com.training.module4_analytics.service.AnalyticsService;
import com.training.module4_analytics.service.DatabaseSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @Autowired
    private com.training.module4_analytics.repository.AuditLogRepository auditLogRepository;

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.training.common.entity.AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/fund-utilization")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<List<FundUtilizationDTO>> getFundUtilization() {
        return ResponseEntity.ok(analyticsService.getFundUtilization());
    }

    @GetMapping("/budget-exhaustion")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<List<BudgetExhaustionDTO>> getBudgetExhaustion() {
        return ResponseEntity.ok(analyticsService.getBudgetExhaustion());
    }

    @GetMapping("/non-compliance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER') or hasRole('DISTRICT_OFFICER')")
    public ResponseEntity<List<NonComplianceDTO>> getNonComplianceSummary() {
        return ResponseEntity.ok(analyticsService.getNonComplianceSummary());
    }

    @GetMapping("/turnaround-times")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
    public ResponseEntity<List<TurnaroundTimeDTO>> getTurnaroundTimes() {
        return ResponseEntity.ok(analyticsService.getTurnaroundTimes());
    }

    @PostMapping("/seed")
    public ResponseEntity<String> forceSeedData() {
        databaseSeeder.seedData();
        return ResponseEntity.ok("Database seeded successfully with sample analytics data.");
    }
}
