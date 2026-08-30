package com.training.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_flags")
public class ComplianceFlag {
    public ComplianceFlag() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    private Long stageId;

    private String flagType;

    private LocalDateTime raisedAt;

    private boolean resolved;

    public ComplianceFlag(Long id, Long applicationId, Long stageId, String flagType, LocalDateTime raisedAt, boolean resolved) {
        this.id = id;
        this.applicationId = applicationId;
        this.stageId = stageId;
        this.flagType = flagType;
        this.raisedAt = raisedAt;
        this.resolved = resolved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }

    public String getFlagType() { return flagType; }
    public void setFlagType(String flagType) { this.flagType = flagType; }

    public LocalDateTime getRaisedAt() { return raisedAt; }
    public void setRaisedAt(LocalDateTime raisedAt) { this.raisedAt = raisedAt; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public static ComplianceFlagBuilder builder() {
        return new ComplianceFlagBuilder();
    }

    public static class ComplianceFlagBuilder {
        private Long id;
        private Long applicationId;
        private Long stageId;
        private String flagType;
        private LocalDateTime raisedAt;
        private boolean resolved;

        public ComplianceFlagBuilder id(Long id) { this.id = id; return this; }
        public ComplianceFlagBuilder applicationId(Long applicationId) { this.applicationId = applicationId; return this; }
        public ComplianceFlagBuilder stageId(Long stageId) { this.stageId = stageId; return this; }
        public ComplianceFlagBuilder flagType(String flagType) { this.flagType = flagType; return this; }
        public ComplianceFlagBuilder raisedAt(LocalDateTime raisedAt) { this.raisedAt = raisedAt; return this; }
        public ComplianceFlagBuilder resolved(boolean resolved) { this.resolved = resolved; return this; }

        public ComplianceFlag build() {
            return new ComplianceFlag(id, applicationId, stageId, flagType, raisedAt, resolved);
        }
    }
}
