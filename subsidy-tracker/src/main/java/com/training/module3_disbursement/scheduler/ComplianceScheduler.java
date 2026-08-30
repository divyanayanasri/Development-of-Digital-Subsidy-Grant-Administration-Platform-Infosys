package com.training.module3_disbursement.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.training.module3_disbursement.service.DisbursementService;

@Component
public class ComplianceScheduler {

    private final DisbursementService disbursementService;

    public ComplianceScheduler(DisbursementService disbursementService) {
        this.disbursementService = disbursementService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkCompliance() {

        System.out.println("Running Daily Compliance Check...");

        disbursementService.checkCompliance();
    }
}