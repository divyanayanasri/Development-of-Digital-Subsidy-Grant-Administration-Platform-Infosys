package com.training.module2_workflow.dto;

import com.training.common.enums.AppStatus;
import com.training.common.enums.Role;
import jakarta.validation.constraints.NotNull;
public class VerificationDecisionDTO {
    public VerificationDecisionDTO() {}

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    @NotNull(message = "Officer role is required")
    private Role role;

    @NotNull(message = "Target status is required")
    private AppStatus targetStatus;

    @NotNull(message = "Decision is required")
    private String decision;

    private String remarks;

    public VerificationDecisionDTO(Long officerId, Role role, AppStatus targetStatus, String decision, String remarks) {
        this.officerId = officerId;
        this.role = role;
        this.targetStatus = targetStatus;
        this.decision = decision;
        this.remarks = remarks;
    }

    public Long getOfficerId() { return officerId; }
    public void setOfficerId(Long officerId) { this.officerId = officerId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public AppStatus getTargetStatus() { return targetStatus; }
    public void setTargetStatus(AppStatus targetStatus) { this.targetStatus = targetStatus; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public static VerificationDecisionDTOBuilder builder() {
        return new VerificationDecisionDTOBuilder();
    }

    public static class VerificationDecisionDTOBuilder {
        private Long officerId;
        private Role role;
        private AppStatus targetStatus;
        private String decision;
        private String remarks;

        public VerificationDecisionDTOBuilder officerId(Long officerId) { this.officerId = officerId; return this; }
        public VerificationDecisionDTOBuilder role(Role role) { this.role = role; return this; }
        public VerificationDecisionDTOBuilder targetStatus(AppStatus targetStatus) { this.targetStatus = targetStatus; return this; }
        public VerificationDecisionDTOBuilder decision(String decision) { this.decision = decision; return this; }
        public VerificationDecisionDTOBuilder remarks(String remarks) { this.remarks = remarks; return this; }

        public VerificationDecisionDTO build() {
            return new VerificationDecisionDTO(officerId, role, targetStatus, decision, remarks);
        }
    }
}
