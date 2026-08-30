package com.training.module2_workflow.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Published when an application transitions to APPROVED.
 * Module 3 (disbursement) should listen for this event with @EventListener
 * instead of being called directly from the workflow service.
 */
public class ApplicationApprovedEvent extends ApplicationEvent {

    private final Long applicationId;
    private final Long beneficiaryId;
    private final Long schemeId;
    private final BigDecimal grantAmount;
    private final LocalDateTime approvedAt;

    public ApplicationApprovedEvent(Object source, Long applicationId, Long beneficiaryId,
                                     Long schemeId, BigDecimal grantAmount, LocalDateTime approvedAt) {
        super(source);
        this.applicationId = applicationId;
        this.beneficiaryId = beneficiaryId;
        this.schemeId = schemeId;
        this.grantAmount = grantAmount;
        this.approvedAt = approvedAt;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public BigDecimal getGrantAmount() {
        return grantAmount;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
}
