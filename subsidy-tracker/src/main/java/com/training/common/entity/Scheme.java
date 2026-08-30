package com.training.common.entity;

import com.training.common.enums.SchemeStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "schemes")
public class Scheme {
    public Scheme() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal minIncome;

    @Column(precision = 12, scale = 2)
    private BigDecimal maxIncome;

    private Double minLandSize;

    private String categoryAllowed;

    @Column(precision = 12, scale = 2)
    private BigDecimal grantAmountMin;

    @Column(precision = 12, scale = 2)
    private BigDecimal grantAmountMax;
  
    private Long regionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchemeStatus status;

    private Long createdBy; // Reference to User.id

    public Scheme(Long id, String name, String description, BigDecimal minIncome, BigDecimal maxIncome, Double minLandSize, String categoryAllowed, BigDecimal grantAmountMin, BigDecimal grantAmountMax, Long regionId, SchemeStatus status, Long createdBy) {
        this.id = id;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public static SchemeBuilder builder() {
        return new SchemeBuilder();
    }

    public static class SchemeBuilder {
        private Long id;
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

        public SchemeBuilder id(Long id) { this.id = id; return this; }
        public SchemeBuilder name(String name) { this.name = name; return this; }
        public SchemeBuilder description(String description) { this.description = description; return this; }
        public SchemeBuilder minIncome(BigDecimal minIncome) { this.minIncome = minIncome; return this; }
        public SchemeBuilder maxIncome(BigDecimal maxIncome) { this.maxIncome = maxIncome; return this; }
        public SchemeBuilder minLandSize(Double minLandSize) { this.minLandSize = minLandSize; return this; }
        public SchemeBuilder categoryAllowed(String categoryAllowed) { this.categoryAllowed = categoryAllowed; return this; }
        public SchemeBuilder grantAmountMin(BigDecimal grantAmountMin) { this.grantAmountMin = grantAmountMin; return this; }
        public SchemeBuilder grantAmountMax(BigDecimal grantAmountMax) { this.grantAmountMax = grantAmountMax; return this; }
        public SchemeBuilder regionId(Long regionId) { this.regionId = regionId; return this; }
        public SchemeBuilder status(SchemeStatus status) { this.status = status; return this; }
        public SchemeBuilder createdBy(Long createdBy) { this.createdBy = createdBy; return this; }

        public Scheme build() {
            return new Scheme(id, name, description, minIncome, maxIncome, minLandSize, categoryAllowed, grantAmountMin, grantAmountMax, regionId, status, createdBy);
        }
    }
}
