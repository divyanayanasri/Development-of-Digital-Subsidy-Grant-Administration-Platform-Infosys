package com.training.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "beneficiaries")
public class Beneficiary {
    public Beneficiary() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String aadharNo;

    private Double landSize;

    @Column(precision = 12, scale = 2)
    private BigDecimal annualIncome;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String address;

    public Beneficiary(Long id, Long userId, String aadharNo, Double landSize, BigDecimal annualIncome, String category, String address) {
        this.id = id;
        this.userId = userId;
        this.aadharNo = aadharNo;
        this.landSize = landSize;
        this.annualIncome = annualIncome;
        this.category = category;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }

    public Double getLandSize() { return landSize; }
    public void setLandSize(Double landSize) { this.landSize = landSize; }

    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public static BeneficiaryBuilder builder() {
        return new BeneficiaryBuilder();
    }

    public static class BeneficiaryBuilder {
        private Long id;
        private Long userId;
        private String aadharNo;
        private Double landSize;
        private BigDecimal annualIncome;
        private String category;
        private String address;

        public BeneficiaryBuilder id(Long id) { this.id = id; return this; }
        public BeneficiaryBuilder userId(Long userId) { this.userId = userId; return this; }
        public BeneficiaryBuilder aadharNo(String aadharNo) { this.aadharNo = aadharNo; return this; }
        public BeneficiaryBuilder landSize(Double landSize) { this.landSize = landSize; return this; }
        public BeneficiaryBuilder annualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; return this; }
        public BeneficiaryBuilder category(String category) { this.category = category; return this; }
        public BeneficiaryBuilder address(String address) { this.address = address; return this; }

        public Beneficiary build() {
            return new Beneficiary(id, userId, aadharNo, landSize, annualIncome, category, address);
        }
    }
}
