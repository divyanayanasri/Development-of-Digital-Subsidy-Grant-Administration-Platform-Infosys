package com.training.module1_masterdata.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class BeneficiaryRegistrationDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Aadhaar number is required")
    private String aadharNo;

    @NotNull(message = "Land size is required")
    @Min(value = 0, message = "Land size must be non-negative")
    private Double landSize;

    @NotNull(message = "Annual income is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Annual income must be non-negative")
    private BigDecimal annualIncome;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Address is required")
    private String address;

    private Long regionId;

    public BeneficiaryRegistrationDTO() {}

    public BeneficiaryRegistrationDTO(String name, String email, String password, String aadharNo, Double landSize, BigDecimal annualIncome, String category, String address, Long regionId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.aadharNo = aadharNo;
        this.landSize = landSize;
        this.annualIncome = annualIncome;
        this.category = category;
        this.address = address;
        this.regionId = regionId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public static BeneficiaryRegistrationDTOBuilder builder() {
        return new BeneficiaryRegistrationDTOBuilder();
    }

    public static class BeneficiaryRegistrationDTOBuilder {
        private String name;
        private String email;
        private String password;
        private String aadharNo;
        private Double landSize;
        private BigDecimal annualIncome;
        private String category;
        private String address;
        private Long regionId;

        public BeneficiaryRegistrationDTOBuilder name(String name) { this.name = name; return this; }
        public BeneficiaryRegistrationDTOBuilder email(String email) { this.email = email; return this; }
        public BeneficiaryRegistrationDTOBuilder password(String password) { this.password = password; return this; }
        public BeneficiaryRegistrationDTOBuilder aadharNo(String aadharNo) { this.aadharNo = aadharNo; return this; }
        public BeneficiaryRegistrationDTOBuilder landSize(Double landSize) { this.landSize = landSize; return this; }
        public BeneficiaryRegistrationDTOBuilder annualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; return this; }
        public BeneficiaryRegistrationDTOBuilder category(String category) { this.category = category; return this; }
        public BeneficiaryRegistrationDTOBuilder address(String address) { this.address = address; return this; }
        public BeneficiaryRegistrationDTOBuilder regionId(Long regionId) { this.regionId = regionId; return this; }

        public BeneficiaryRegistrationDTO build() {
            return new BeneficiaryRegistrationDTO(name, email, password, aadharNo, landSize, annualIncome, category, address, regionId);
        }
    }
}
