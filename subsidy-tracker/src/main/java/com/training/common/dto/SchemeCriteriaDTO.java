package com.training.common.dto;

import java.math.BigDecimal;
public class SchemeCriteriaDTO {
    public SchemeCriteriaDTO() {}

    private Long schemeId;
    private BigDecimal minIncome;
    private BigDecimal maxIncome;
    private Double minLandSize;
    private String categoryAllowed;
    private BigDecimal grantAmountMin;
    private BigDecimal grantAmountMax;

    public SchemeCriteriaDTO(Long schemeId, BigDecimal minIncome, BigDecimal maxIncome, Double minLandSize, String categoryAllowed, BigDecimal grantAmountMin, BigDecimal grantAmountMax) {
        this.schemeId = schemeId;
        this.minIncome = minIncome;
        this.maxIncome = maxIncome;
        this.minLandSize = minLandSize;
        this.categoryAllowed = categoryAllowed;
        this.grantAmountMin = grantAmountMin;
        this.grantAmountMax = grantAmountMax;
    }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public BigDecimal getMinIncome() { return minIncome; }
    public void setMinIncome(BigDecimal minIncome) { this.minIncome = minIncome; }

    public BigDecimal getMaxIncome() { return maxIncome; }
    public void setMaxIncome(BigDecimal maxIncome) { this.maxIncome = maxIncome; }

    public Double getMinLandSize() { return minLandSize; }
    public void setMinLandSize(Double minLandSize) { this.minLandSize = minLandSize; }

    public String getCategoryAllowed() { return categoryAllowed; }
    public void setCategoryAllowed(String categoryAllowed) { this.categoryAllowed = categoryAllowed; }

    public BigDecimal getGrantAmountMin() { return grantAmountMin; }
    public void setGrantAmountMin(BigDecimal grantAmountMin) { this.grantAmountMin = grantAmountMin; }

    public BigDecimal getGrantAmountMax() { return grantAmountMax; }
    public void setGrantAmountMax(BigDecimal grantAmountMax) { this.grantAmountMax = grantAmountMax; }

    public static SchemeCriteriaDTOBuilder builder() {
        return new SchemeCriteriaDTOBuilder();
    }

    public static class SchemeCriteriaDTOBuilder {
        private Long schemeId;
        private BigDecimal minIncome;
        private BigDecimal maxIncome;
        private Double minLandSize;
        private String categoryAllowed;
        private BigDecimal grantAmountMin;
        private BigDecimal grantAmountMax;

        public SchemeCriteriaDTOBuilder schemeId(Long schemeId) { this.schemeId = schemeId; return this; }
        public SchemeCriteriaDTOBuilder minIncome(BigDecimal minIncome) { this.minIncome = minIncome; return this; }
        public SchemeCriteriaDTOBuilder maxIncome(BigDecimal maxIncome) { this.maxIncome = maxIncome; return this; }
        public SchemeCriteriaDTOBuilder minLandSize(Double minLandSize) { this.minLandSize = minLandSize; return this; }
        public SchemeCriteriaDTOBuilder categoryAllowed(String categoryAllowed) { this.categoryAllowed = categoryAllowed; return this; }
        public SchemeCriteriaDTOBuilder grantAmountMin(BigDecimal grantAmountMin) { this.grantAmountMin = grantAmountMin; return this; }
        public SchemeCriteriaDTOBuilder grantAmountMax(BigDecimal grantAmountMax) { this.grantAmountMax = grantAmountMax; return this; }

        public SchemeCriteriaDTO build() {
            return new SchemeCriteriaDTO(schemeId, minIncome, maxIncome, minLandSize, categoryAllowed, grantAmountMin, grantAmountMax);
        }
    }
}
