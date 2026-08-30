package com.training;

import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.common.dto.SchemeCriteriaDTO;
import com.training.common.entity.Beneficiary;
import com.training.common.entity.Document;
import com.training.common.entity.Region;
import com.training.common.entity.Scheme;
import com.training.common.enums.Role;
import com.training.common.enums.SchemeStatus;
import com.training.module1_masterdata.dto.BeneficiaryRegistrationDTO;
import com.training.module1_masterdata.dto.DocumentUploadDTO;
import com.training.module1_masterdata.dto.SchemeDTO;
import com.training.module1_masterdata.repository.RegionRepository;
import com.training.module1_masterdata.service.BeneficiaryService;
import com.training.module1_masterdata.service.DocumentService;
import com.training.module1_masterdata.service.SchemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SubsidyTrackerApplicationTests {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RegionRepository regionRepository;

    private Long regionId;

    @BeforeEach
    public void setup() {
        Region region = Region.builder()
                .name("South Region")
                .budgetCap(new BigDecimal("1000000.00"))
                .budgetUsed(new BigDecimal("0.00"))
                .build();
        region = regionRepository.save(region);
        regionId = region.getId();
    }

    @Test
    public void testBeneficiaryRegistration_Success() {
        BeneficiaryRegistrationDTO dto = BeneficiaryRegistrationDTO.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("securepassword")
                .aadharNo("123456789012")
                .landSize(4.5)
                .annualIncome(new BigDecimal("150000.00"))
                .category("General")
                .address("123 Main St, South Region")
                .regionId(regionId)
                .build();

        Beneficiary beneficiary = beneficiaryService.registerBeneficiary(dto);
        assertNotNull(beneficiary.getId());
        assertEquals("123456789012", beneficiary.getAadharNo());
        assertEquals(4.5, beneficiary.getLandSize());
        assertEquals(new BigDecimal("150000.00"), beneficiary.getAnnualIncome());

        BeneficiaryEligibilityDTO eligibility = beneficiaryService.getBeneficiaryEligibility(beneficiary.getId());
        assertNotNull(eligibility);
        assertEquals(beneficiary.getId(), eligibility.getBeneficiaryId());
        assertEquals(beneficiary.getAnnualIncome(), eligibility.getIncome());
        assertEquals(beneficiary.getLandSize(), eligibility.getLandSize());
        assertEquals("General", eligibility.getCategory());
        assertEquals(regionId, eligibility.getRegionId());
    }

    @Test
    public void testBeneficiaryRegistration_DuplicateEmail() {
        BeneficiaryRegistrationDTO dto1 = BeneficiaryRegistrationDTO.builder()
                .name("John Doe")
                .email("john.dup@example.com")
                .password("password")
                .aadharNo("123456789012")
                .landSize(2.0)
                .annualIncome(new BigDecimal("50000.00"))
                .category("OBC")
                .address("Some Address")
                .regionId(regionId)
                .build();

        beneficiaryService.registerBeneficiary(dto1);

        BeneficiaryRegistrationDTO dto2 = BeneficiaryRegistrationDTO.builder()
                .name("Jane Doe")
                .email("john.dup@example.com")
                .password("password")
                .aadharNo("987654321098")
                .landSize(1.0)
                .annualIncome(new BigDecimal("60000.00"))
                .category("OBC")
                .address("Another Address")
                .regionId(regionId)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            beneficiaryService.registerBeneficiary(dto2);
        });
    }

    @Test
    public void testBeneficiaryRegistration_InvalidFormats() {
        BeneficiaryRegistrationDTO dto1 = BeneficiaryRegistrationDTO.builder()
                .name("John Neg")
                .email("john.neg@example.com")
                .password("password")
                .aadharNo("111122223333")
                .landSize(2.0)
                .annualIncome(new BigDecimal("-50000.00"))
                .category("General")
                .address("Address")
                .regionId(regionId)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            beneficiaryService.registerBeneficiary(dto1);
        });

        BeneficiaryRegistrationDTO dto2 = BeneficiaryRegistrationDTO.builder()
                .name("John Neg Land")
                .email("john.negland@example.com")
                .password("password")
                .aadharNo("111122223334")
                .landSize(-2.0)
                .annualIncome(new BigDecimal("50000.00"))
                .category("General")
                .address("Address")
                .regionId(regionId)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            beneficiaryService.registerBeneficiary(dto2);
        });
    }

    @Test
    public void testDocumentUploadAndRequiredChecks() {
        BeneficiaryRegistrationDTO dto = BeneficiaryRegistrationDTO.builder()
                .name("Alice Smith")
                .email("alice.smith@example.com")
                .password("password")
                .aadharNo("555566667777")
                .landSize(3.0)
                .annualIncome(new BigDecimal("120000.00"))
                .category("SC")
                .address("Alice's Address")
                .regionId(regionId)
                .build();

        Beneficiary beneficiary = beneficiaryService.registerBeneficiary(dto);
        Long benId = beneficiary.getId();

        assertFalse(documentService.checkRequiredDocuments(benId));

        DocumentUploadDTO doc1 = new DocumentUploadDTO("Aadhaar Card", "/docs/aadhaar.pdf");
        documentService.uploadDocument(benId, doc1);
        assertFalse(documentService.checkRequiredDocuments(benId));

        documentService.uploadDocument(benId, new DocumentUploadDTO("PAN Card", "/docs/pan.pdf"));
        documentService.uploadDocument(benId, new DocumentUploadDTO("Electricity Bill", "/docs/electricity.pdf"));
        documentService.uploadDocument(benId, new DocumentUploadDTO("Property Ownership Proof", "/docs/property.pdf"));
        documentService.uploadDocument(benId, new DocumentUploadDTO("Bank Account Details", "/docs/bank.pdf"));
        documentService.uploadDocument(benId, new DocumentUploadDTO("Passport-size Photograph", "/docs/photo.jpg"));

        assertTrue(documentService.checkRequiredDocuments(benId));

        List<Document> docs = documentService.getDocumentsByBeneficiaryId(benId);
        assertEquals(6, docs.size());
    }

    @Test
    public void testSchemeManagement() {
        SchemeDTO dto = SchemeDTO.builder()
                .name("Solar Sub Program")
                .description("Solar subsidy scheme details")
                .minIncome(new BigDecimal("10000.00"))
                .maxIncome(new BigDecimal("200000.00"))
                .minLandSize(1.0)
                .categoryAllowed("General,OBC,SC,ST")
                .grantAmountMin(new BigDecimal("25000.00"))
                .grantAmountMax(new BigDecimal("50000.00"))
                .regionId(regionId)
                .status(SchemeStatus.ACTIVE)
                .createdBy(1L)
                .build();

        Scheme scheme = schemeService.createScheme(dto);
        assertNotNull(scheme.getId());
        assertEquals("Solar Sub Program", scheme.getName());
        assertEquals(SchemeStatus.ACTIVE, scheme.getStatus());

        SchemeCriteriaDTO criteria = schemeService.getSchemeCriteria(scheme.getId());
        assertNotNull(criteria);
        assertEquals(scheme.getId(), criteria.getSchemeId());
        assertEquals(new BigDecimal("10000.00"), criteria.getMinIncome());
        assertEquals(1.0, criteria.getMinLandSize());

        dto.setName("Solar Sub Program V2");
        dto.setStatus(SchemeStatus.INACTIVE);
        Scheme updated = schemeService.updateScheme(scheme.getId(), dto);
        assertEquals("Solar Sub Program V2", updated.getName());
        assertEquals(SchemeStatus.INACTIVE, updated.getStatus());
    }

    @Test
    public void testBeneficiaryRegistration_InvalidRegion() {
        BeneficiaryRegistrationDTO dto = BeneficiaryRegistrationDTO.builder()
                .name("John Doe")
                .email("john.invalidregion@example.com")
                .password("securepassword")
                .aadharNo("123456789019")
                .landSize(4.5)
                .annualIncome(new BigDecimal("150000.00"))
                .category("General")
                .address("123 Main St, Invalid Region")
                .regionId(9999L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            beneficiaryService.registerBeneficiary(dto);
        });
    }

    @Test
    public void testSchemeManagement_InvalidIncomeRange() {
        SchemeDTO dto = SchemeDTO.builder()
                .name("Invalid Income Scheme")
                .minIncome(new BigDecimal("200000.00"))
                .maxIncome(new BigDecimal("100000.00"))
                .minLandSize(1.0)
                .categoryAllowed("General")
                .grantAmountMin(new BigDecimal("25000.00"))
                .grantAmountMax(new BigDecimal("50000.00"))
                .regionId(regionId)
                .status(SchemeStatus.ACTIVE)
                .createdBy(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            schemeService.createScheme(dto);
        });
    }

    @Test
    public void testSchemeManagement_InvalidGrantRange() {
        SchemeDTO dto = SchemeDTO.builder()
                .name("Invalid Grant Scheme")
                .minIncome(new BigDecimal("10000.00"))
                .maxIncome(new BigDecimal("200000.00"))
                .minLandSize(1.0)
                .categoryAllowed("General")
                .grantAmountMin(new BigDecimal("50000.00"))
                .grantAmountMax(new BigDecimal("25000.00"))
                .regionId(regionId)
                .status(SchemeStatus.ACTIVE)
                .createdBy(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            schemeService.createScheme(dto);
        });
    }

    @Test
    public void testSchemeManagement_InvalidRegion() {
        SchemeDTO dto = SchemeDTO.builder()
                .name("Invalid Region Scheme")
                .minIncome(new BigDecimal("10000.00"))
                .maxIncome(new BigDecimal("200000.00"))
                .minLandSize(1.0)
                .categoryAllowed("General")
                .grantAmountMin(new BigDecimal("25000.00"))
                .grantAmountMax(new BigDecimal("50000.00"))
                .regionId(9999L)
                .status(SchemeStatus.ACTIVE)
                .createdBy(1L)
                .build();

        assertThrows(IllegalArgumentException.class, () -> {
            schemeService.createScheme(dto);
        });
    }
}
