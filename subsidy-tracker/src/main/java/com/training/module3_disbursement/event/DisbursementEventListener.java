package com.training.module3_disbursement.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.training.module2_workflow.event.ApplicationApprovedEvent;
import com.training.module3_disbursement.service.DisbursementService;

@Component
public class DisbursementEventListener {

    private final DisbursementService service;

    public DisbursementEventListener(DisbursementService service) {
        this.service = service;
    }

    @EventListener
    public void handleApplicationApproved(ApplicationApprovedEvent event) {

        service.createDisbursementPlan(event.getApplicationId());

        System.out.println(
                "Disbursement Plan created for Application "
                        + event.getApplicationId());
    }
}