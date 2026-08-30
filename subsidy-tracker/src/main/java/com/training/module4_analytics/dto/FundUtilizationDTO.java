package com.training.module4_analytics.dto;

import java.math.BigDecimal;
public class FundUtilizationDTO {
    public FundUtilizationDTO() {}

    private Long schemeId;
    private String schemeName;
    private BigDecimal allocatedAmount;
    private BigDecimal releasedAmount;

    public FundUtilizationDTO(Long schemeId, String schemeName, BigDecimal allocatedAmount, BigDecimal releasedAmount) {
        this.schemeId = schemeId;
        this.schemeName = schemeName;
        this.allocatedAmount = allocatedAmount;
        this.releasedAmount = releasedAmount;
    }

    public Long getSchemeId() { return schemeId; }
    public void setSchemeId(Long schemeId) { this.schemeId = schemeId; }

    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }

    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public BigDecimal getReleasedAmount() { return releasedAmount; }
    public void setReleasedAmount(BigDecimal releasedAmount) { this.releasedAmount = releasedAmount; }

    public static FundUtilizationDTOBuilder builder() {
        return new FundUtilizationDTOBuilder();
    }

    public static class FundUtilizationDTOBuilder {
        private Long schemeId;
        private String schemeName;
        private BigDecimal allocatedAmount;
        private BigDecimal releasedAmount;

        public FundUtilizationDTOBuilder schemeId(Long schemeId) { this.schemeId = schemeId; return this; }
        public FundUtilizationDTOBuilder schemeName(String schemeName) { this.schemeName = schemeName; return this; }
        public FundUtilizationDTOBuilder allocatedAmount(BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; return this; }
        public FundUtilizationDTOBuilder releasedAmount(BigDecimal releasedAmount) { this.releasedAmount = releasedAmount; return this; }

        public FundUtilizationDTO build() {
            return new FundUtilizationDTO(schemeId, schemeName, allocatedAmount, releasedAmount);
        }
    }
}
