package com.training.common.entity;

import com.training.common.enums.StageStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "disbursement_stages")
public class DisbursementStage {
    public DisbursementStage() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long planId;

    private Integer stageNo;

    private String milestoneName;

    private Double percentage;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StageStatus status;

    private LocalDateTime releasedAt;

    public DisbursementStage(Long id, Long planId, Integer stageNo, String milestoneName, Double percentage, BigDecimal amount, LocalDate dueDate, StageStatus status, LocalDateTime releasedAt) {
        this.id = id;
        this.planId = planId;
        this.stageNo = stageNo;
        this.milestoneName = milestoneName;
        this.percentage = percentage;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.releasedAt = releasedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public Integer getStageNo() { return stageNo; }
    public void setStageNo(Integer stageNo) { this.stageNo = stageNo; }

    public String getMilestoneName() { return milestoneName; }
    public void setMilestoneName(String milestoneName) { this.milestoneName = milestoneName; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public StageStatus getStatus() { return status; }
    public void setStatus(StageStatus status) { this.status = status; }

    public LocalDateTime getReleasedAt() { return releasedAt; }
    public void setReleasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; }

    public static DisbursementStageBuilder builder() {
        return new DisbursementStageBuilder();
    }

    public static class DisbursementStageBuilder {
        private Long id;
        private Long planId;
        private Integer stageNo;
        private String milestoneName;
        private Double percentage;
        private BigDecimal amount;
        private LocalDate dueDate;
        private StageStatus status;
        private LocalDateTime releasedAt;

        public DisbursementStageBuilder id(Long id) { this.id = id; return this; }
        public DisbursementStageBuilder planId(Long planId) { this.planId = planId; return this; }
        public DisbursementStageBuilder stageNo(Integer stageNo) { this.stageNo = stageNo; return this; }
        public DisbursementStageBuilder milestoneName(String milestoneName) { this.milestoneName = milestoneName; return this; }
        public DisbursementStageBuilder percentage(Double percentage) { this.percentage = percentage; return this; }
        public DisbursementStageBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public DisbursementStageBuilder dueDate(LocalDate dueDate) { this.dueDate = dueDate; return this; }
        public DisbursementStageBuilder status(StageStatus status) { this.status = status; return this; }
        public DisbursementStageBuilder releasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; return this; }

        public DisbursementStage build() {
            return new DisbursementStage(id, planId, stageNo, milestoneName, percentage, amount, dueDate, status, releasedAt);
        }
    }
}
