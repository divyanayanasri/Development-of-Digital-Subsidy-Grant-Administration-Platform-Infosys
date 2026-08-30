package com.training.module4_analytics.dto;

import java.math.BigDecimal;
public class BudgetExhaustionDTO {
    public BudgetExhaustionDTO() {}

    private Long regionId;
    private String regionName;
    private BigDecimal budgetCap;
    private BigDecimal budgetUsed;
    private BigDecimal calculatedReleased;
    private Double exhaustionPercentage;

    public BudgetExhaustionDTO(Long regionId, String regionName, BigDecimal budgetCap, BigDecimal budgetUsed, BigDecimal calculatedReleased, Double exhaustionPercentage) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.budgetCap = budgetCap;
        this.budgetUsed = budgetUsed;
        this.calculatedReleased = calculatedReleased;
        this.exhaustionPercentage = exhaustionPercentage;
    }

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public BigDecimal getBudgetCap() { return budgetCap; }
    public void setBudgetCap(BigDecimal budgetCap) { this.budgetCap = budgetCap; }

    public BigDecimal getBudgetUsed() { return budgetUsed; }
    public void setBudgetUsed(BigDecimal budgetUsed) { this.budgetUsed = budgetUsed; }

    public BigDecimal getCalculatedReleased() { return calculatedReleased; }
    public void setCalculatedReleased(BigDecimal calculatedReleased) { this.calculatedReleased = calculatedReleased; }

    public Double getExhaustionPercentage() { return exhaustionPercentage; }
    public void setExhaustionPercentage(Double exhaustionPercentage) { this.exhaustionPercentage = exhaustionPercentage; }

    public static BudgetExhaustionDTOBuilder builder() {
        return new BudgetExhaustionDTOBuilder();
    }

    public static class BudgetExhaustionDTOBuilder {
        private Long regionId;
        private String regionName;
        private BigDecimal budgetCap;
        private BigDecimal budgetUsed;
        private BigDecimal calculatedReleased;
        private Double exhaustionPercentage;

        public BudgetExhaustionDTOBuilder regionId(Long regionId) { this.regionId = regionId; return this; }
        public BudgetExhaustionDTOBuilder regionName(String regionName) { this.regionName = regionName; return this; }
        public BudgetExhaustionDTOBuilder budgetCap(BigDecimal budgetCap) { this.budgetCap = budgetCap; return this; }
        public BudgetExhaustionDTOBuilder budgetUsed(BigDecimal budgetUsed) { this.budgetUsed = budgetUsed; return this; }
        public BudgetExhaustionDTOBuilder calculatedReleased(BigDecimal calculatedReleased) { this.calculatedReleased = calculatedReleased; return this; }
        public BudgetExhaustionDTOBuilder exhaustionPercentage(Double exhaustionPercentage) { this.exhaustionPercentage = exhaustionPercentage; return this; }

        public BudgetExhaustionDTO build() {
            return new BudgetExhaustionDTO(regionId, regionName, budgetCap, budgetUsed, calculatedReleased, exhaustionPercentage);
        }
    }
}