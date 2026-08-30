package com.training.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "disbursement_plans")
public class DisbursementPlan {
    public DisbursementPlan() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    public DisbursementPlan(Long id, Long applicationId, BigDecimal totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static DisbursementPlanBuilder builder() {
        return new DisbursementPlanBuilder();
    }

    public static class DisbursementPlanBuilder {
        private Long id;
        private Long applicationId;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;

        public DisbursementPlanBuilder id(Long id) { this.id = id; return this; }
        public DisbursementPlanBuilder applicationId(Long applicationId) { this.applicationId = applicationId; return this; }
        public DisbursementPlanBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public DisbursementPlanBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DisbursementPlan build() {
            return new DisbursementPlan(id, applicationId, totalAmount, createdAt);
        }
    }
}
