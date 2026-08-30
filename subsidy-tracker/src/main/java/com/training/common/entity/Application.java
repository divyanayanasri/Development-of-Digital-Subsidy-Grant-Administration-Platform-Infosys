package com.training.common.entity;

import com.training.common.enums.AppStatus;
import com.training.common.enums.RouteType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {
    public Application() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long beneficiaryId;

    @Column(nullable = false)
    private Long schemeId;

    private Integer eligibilityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppStatus status;

    @Enumerated(EnumType.STRING)
    private RouteType routeType;

    private Long assignedOfficerId;

    private LocalDateTime submittedAt;

    private LocalDateTime decidedAt;

    public Application(Long id, Long beneficiaryId, Long schemeId, Integer eligibilityScore, AppStatus status, RouteType routeType, Long assignedOfficerId, LocalDateTime submittedAt, LocalDateTime decidedAt) {
        this.id = id;
        this.beneficiaryId = beneficiaryId;
        this.schemeId = schemeId;
        this.eligibilityScore = eligibilityScore;
        this.status = status;
        this.routeType = routeType;
        this.assignedOfficerId = assignedOfficerId;
        this.submittedAt = submittedAt;
        this.decidedAt = decidedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public Integer getEligibilityScore() { return eligibilityScore; }
    public void setEligibilityScore(Integer eligibilityScore) { this.eligibilityScore = eligibilityScore; }

    public AppStatus getStatus() { return status; }
    public void setStatus(AppStatus status) { this.status = status; }

    public RouteType getRouteType() { return routeType; }
    public void setRouteType(RouteType routeType) { this.routeType = routeType; }

    public Long getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(Long assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }

    public static ApplicationBuilder builder() {
        return new ApplicationBuilder();
    }

    public static class ApplicationBuilder {
        private Long id;
        private Long beneficiaryId;
        private Long schemeId;
        private Integer eligibilityScore;
        private AppStatus status;
        private RouteType routeType;
        private Long assignedOfficerId;
        private LocalDateTime submittedAt;
        private LocalDateTime decidedAt;

        public ApplicationBuilder id(Long id) { this.id = id; return this; }
        public ApplicationBuilder beneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; return this; }
        public ApplicationBuilder schemeId(Long schemeId) { this.schemeId = schemeId; return this; }
        public ApplicationBuilder eligibilityScore(Integer eligibilityScore) { this.eligibilityScore = eligibilityScore; return this; }
        public ApplicationBuilder status(AppStatus status) { this.status = status; return this; }
        public ApplicationBuilder routeType(RouteType routeType) { this.routeType = routeType; return this; }
        public ApplicationBuilder assignedOfficerId(Long assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; return this; }
        public ApplicationBuilder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public ApplicationBuilder decidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; return this; }

        public Application build() {
            return new Application(id, beneficiaryId, schemeId, eligibilityScore, status, routeType, assignedOfficerId, submittedAt, decidedAt);
        }
    }
}
