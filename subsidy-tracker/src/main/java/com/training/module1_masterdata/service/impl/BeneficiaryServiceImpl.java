package com.training.module1_masterdata.service.impl;

import com.training.common.entity.User;
import com.training.common.entity.Beneficiary;
import com.training.common.entity.Application;
import com.training.common.enums.Role;
import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.common.exception.ResourceNotFoundException;
import com.training.module1_masterdata.dto.BeneficiaryRegistrationDTO;
import com.training.module1_masterdata.repository.UserRepository;
import com.training.module1_masterdata.repository.BeneficiaryRepository;
import com.training.module1_masterdata.repository.RegionRepository;
import com.training.module2_workflow.repository.ApplicationRepository;
import com.training.module1_masterdata.service.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BeneficiaryServiceImpl implements BeneficiaryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.training.common.service.AuditLogService auditLogService;

    @Override
    public Beneficiary registerBeneficiary(BeneficiaryRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (beneficiaryRepository.existsByAadharNo(dto.getAadharNo())) {
            throw new IllegalArgumentException("Aadhaar number is already registered");
        }
        if (dto.getRegionId() != null && !regionRepository.existsById(dto.getRegionId())) {
            throw new IllegalArgumentException("Region not found with ID: " + dto.getRegionId());
        }

        if (dto.getAnnualIncome().doubleValue() < 0) {
            throw new IllegalArgumentException("Annual income must be non-negative");
        }
        if (dto.getLandSize() < 0) {
            throw new IllegalArgumentException("Land size must be non-negative");
        }

        // Create User
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .role(Role.BENEFICIARY)
                .regionId(dto.getRegionId())
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        // Create Beneficiary
        Beneficiary beneficiary = Beneficiary.builder()
                .userId(user.getId())
                .aadharNo(dto.getAadharNo())
                .landSize(dto.getLandSize())
                .annualIncome(dto.getAnnualIncome())
                .category(dto.getCategory())
                .address(dto.getAddress())
                .build();

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        auditLogService.log("REGISTER_BENEFICIARY", "Beneficiary", saved.getId(), "Registered beneficiary: " + dto.getName() + " with User ID: " + user.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Beneficiary getBeneficiaryById(Long id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryEligibilityDTO getBeneficiaryEligibility(Long id) {
        Beneficiary beneficiary = getBeneficiaryById(id);
        User user = userRepository.findById(beneficiary.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Associated user not found for beneficiary ID: " + id));
        
        return BeneficiaryEligibilityDTO.builder()
                .beneficiaryId(beneficiary.getId())
                .income(beneficiary.getAnnualIncome())
                .landSize(beneficiary.getLandSize())
                .category(beneficiary.getCategory())
                .regionId(user.getRegionId())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsByBeneficiaryId(Long id) {
        if (!beneficiaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beneficiary not found with ID: " + id);
        }
        return applicationRepository.findByBeneficiaryId(id);
    }
}
