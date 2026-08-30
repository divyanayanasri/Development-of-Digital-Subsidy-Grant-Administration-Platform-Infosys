package com.training.module2_workflow.dto;

import jakarta.validation.constraints.NotNull;
public class ApplicationSubmissionDTO {
    public ApplicationSubmissionDTO() {}

    @NotNull(message = "Beneficiary ID is required")
    private Long beneficiaryId;

    @NotNull(message = "Scheme ID is required")
    private Long schemeId;

    public ApplicationSubmissionDTO(Long beneficiaryId, Long schemeId) {
        this.beneficiaryId = beneficiaryId;
        this.schemeId = schemeId;
    }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public static ApplicationSubmissionDTOBuilder builder() {
        return new ApplicationSubmissionDTOBuilder();
    }

    public static class ApplicationSubmissionDTOBuilder {
        private Long beneficiaryId;
        private Long schemeId;

        public ApplicationSubmissionDTOBuilder beneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; return this; }
        public ApplicationSubmissionDTOBuilder schemeId(Long schemeId) { this.schemeId = schemeId; return this; }

        public ApplicationSubmissionDTO build() {
            return new ApplicationSubmissionDTO(beneficiaryId, schemeId);
        }
    }
}
