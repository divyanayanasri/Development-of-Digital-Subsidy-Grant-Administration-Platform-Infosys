package com.training.common.dto;

import java.math.BigDecimal;
public class BeneficiaryEligibilityDTO {
    public BeneficiaryEligibilityDTO() {}

    private Long beneficiaryId;
    private BigDecimal income;
    private Double landSize;
    private String category;
    private Long regionId;

    public BeneficiaryEligibilityDTO(Long beneficiaryId, BigDecimal income, Double landSize, String category, Long regionId) {
        this.beneficiaryId = beneficiaryId;
        this.income = income;
        this.landSize = landSize;
        this.category = category;
        this.regionId = regionId;
    }

    public Long getBeneficiaryId() { return beneficiaryId; }
    public void setBeneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; }

    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }

    public Double getLandSize() { return landSize; }
    public void setLandSize(Double landSize) { this.landSize = landSize; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public static BeneficiaryEligibilityDTOBuilder builder() {
        return new BeneficiaryEligibilityDTOBuilder();
    }

    public static class BeneficiaryEligibilityDTOBuilder {
        private Long beneficiaryId;
        private BigDecimal income;
        private Double landSize;
        private String category;
        private Long regionId;

        public BeneficiaryEligibilityDTOBuilder beneficiaryId(Long beneficiaryId) { this.beneficiaryId = beneficiaryId; return this; }
        public BeneficiaryEligibilityDTOBuilder income(BigDecimal income) { this.income = income; return this; }
        public BeneficiaryEligibilityDTOBuilder landSize(Double landSize) { this.landSize = landSize; return this; }
        public BeneficiaryEligibilityDTOBuilder category(String category) { this.category = category; return this; }
        public BeneficiaryEligibilityDTOBuilder regionId(Long regionId) { this.regionId = regionId; return this; }

        public BeneficiaryEligibilityDTO build() {
            return new BeneficiaryEligibilityDTO(beneficiaryId, income, landSize, category, regionId);
        }
    }
}
