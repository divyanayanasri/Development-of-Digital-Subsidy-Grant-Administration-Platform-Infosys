package com.training.common.entity;

import com.training.common.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_logs")
public class VerificationLog {
    public VerificationLog() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private Long officerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String decision;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private LocalDateTime createdAt;

    public VerificationLog(Long id, Long applicationId, Long officerId, Role role, String decision, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.officerId = officerId;
        this.role = role;
        this.decision = decision;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Long getOfficerId() { return officerId; }
    public void setOfficerId(Long officerId) { this.officerId = officerId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static VerificationLogBuilder builder() {
        return new VerificationLogBuilder();
    }

    public static class VerificationLogBuilder {
        private Long id;
        private Long applicationId;
        private Long officerId;
        private Role role;
        private String decision;
        private String remarks;
        private LocalDateTime createdAt;

        public VerificationLogBuilder id(Long id) { this.id = id; return this; }
        public VerificationLogBuilder applicationId(Long applicationId) { this.applicationId = applicationId; return this; }
        public VerificationLogBuilder officerId(Long officerId) { this.officerId = officerId; return this; }
        public VerificationLogBuilder role(Role role) { this.role = role; return this; }
        public VerificationLogBuilder decision(String decision) { this.decision = decision; return this; }
        public VerificationLogBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public VerificationLogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public VerificationLog build() {
            return new VerificationLog(id, applicationId, officerId, role, decision, remarks, createdAt);
        }
    }
}
