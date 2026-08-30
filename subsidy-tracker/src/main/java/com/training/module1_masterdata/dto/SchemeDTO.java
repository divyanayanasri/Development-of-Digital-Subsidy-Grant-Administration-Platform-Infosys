package com.training.module1_masterdata.dto;

import com.training.common.enums.SchemeStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class SchemeDTO {
    @NotBlank(message = "Scheme name is required")
    private String name;

    private String description;

    @NotNull(message = "Minimum income is required")
    @DecimalMin(value = "0.0", message = "Minimum income must be non-negative")
    private BigDecimal minIncome;

    @NotNull(message = "Maximum income is required")
    @DecimalMin(value = "0.0", message = "Maximum income must be non-negative")
    private BigDecimal maxIncome;

    @NotNull(message = "Minimum land size is required")
    @Min(value = 0, message = "Minimum land size must be non-negative")
    private Double minLandSize;

    @NotBlank(message = "Category allowed is required")
    private String categoryAllowed;

    @NotNull(message = "Grant amount min is required")
    @DecimalMin(value = "0.0", message = "Grant amount min must be non-negative")
    private BigDecimal grantAmountMin;

    @NotNull(message = "Grant amount max is required")
    @DecimalMin(value = "0.0", message = "Grant amount max must be non-negative")
    private BigDecimal grantAmountMax;

    private Long regionId;

    @NotNull(message = "Scheme status is required")
    private SchemeStatus status;

    private Long createdBy;

    public SchemeDTO() {}

    public SchemeDTO(String name, String description, BigDecimal minIncome, BigDecimal maxIncome, Double minLandSize, String categoryAllowed, BigDecimal grantAmountMin, BigDecimal grantAmountMax, Long regionId, SchemeStatus status, Long createdBy) {
        this.name = name;
        this.description = description;
        this.minIncome = minIncome;
        this.maxIncome = maxIncome;
        this.minLandSize = minLandSize;
        this.categoryAllowed = categoryAllowed;
        this.grantAmountMin = grantAmountMin;
        this.grantAmountMax = grantAmountMax;
        this.regionId = regionId;
        this.status = status;
        this.createdBy = createdBy;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public SchemeStatus getStatus() { return status; }
    public void setStatus(SchemeStatus status) { this.status = status; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public static SchemeDTOBuilder builder() {
        return new SchemeDTOBuilder();
    }

    public static class SchemeDTOBuilder {
        private String name;
        private String description;
        private BigDecimal minIncome;
        private BigDecimal maxIncome;
        private Double minLandSize;
        private String categoryAllowed;
        private BigDecimal grantAmountMin;
        private BigDecimal grantAmountMax;
        private Long regionId;
        private SchemeStatus status;
        private Long createdBy;

        public SchemeDTOBuilder name(String name) { this.name = name; return this; }
        public SchemeDTOBuilder description(String description) { this.description = description; return this; }
        public SchemeDTOBuilder minIncome(BigDecimal minIncome) { this.minIncome = minIncome; return this; }
        public SchemeDTOBuilder maxIncome(BigDecimal maxIncome) { this.maxIncome = maxIncome; return this; }
        public SchemeDTOBuilder minLandSize(Double minLandSize) { this.minLandSize = minLandSize; return this; }
        public SchemeDTOBuilder categoryAllowed(String categoryAllowed) { this.categoryAllowed = categoryAllowed; return this; }
        public SchemeDTOBuilder grantAmountMin(BigDecimal grantAmountMin) { this.grantAmountMin = grantAmountMin; return this; }
        public SchemeDTOBuilder grantAmountMax(BigDecimal grantAmountMax) { this.grantAmountMax = grantAmountMax; return this; }
        public SchemeDTOBuilder regionId(Long regionId) { this.regionId = regionId; return this; }
        public SchemeDTOBuilder status(SchemeStatus status) { this.status = status; return this; }
        public SchemeDTOBuilder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }

        public SchemeDTO build() {
            return new SchemeDTO(name, description, minIncome, maxIncome, minLandSize, categoryAllowed, grantAmountMin, grantAmountMax, regionId, status, createdBy);
        }
    }
}
