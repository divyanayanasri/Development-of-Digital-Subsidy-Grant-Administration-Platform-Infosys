package com.training.module1_masterdata.service.impl;

import com.training.common.entity.Scheme;
import com.training.common.dto.SchemeCriteriaDTO;
import com.training.common.exception.ResourceNotFoundException;
import com.training.module1_masterdata.dto.SchemeDTO;
import com.training.module1_masterdata.repository.SchemeRepository;
import com.training.module1_masterdata.repository.RegionRepository;
import com.training.module1_masterdata.service.SchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SchemeServiceImpl implements SchemeService {

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private com.training.common.service.AuditLogService auditLogService;

    private void validateSchemeDTO(SchemeDTO dto) {
        if (dto.getMinIncome() != null && dto.getMaxIncome() != null && dto.getMinIncome().compareTo(dto.getMaxIncome()) > 0) {
            throw new IllegalArgumentException("Minimum income cannot be greater than maximum income");
        }
        if (dto.getGrantAmountMin() != null && dto.getGrantAmountMax() != null && dto.getGrantAmountMin().compareTo(dto.getGrantAmountMax()) > 0) {
            throw new IllegalArgumentException("Minimum grant amount cannot be greater than maximum grant amount");
        }
        if (dto.getRegionId() != null && !regionRepository.existsById(dto.getRegionId())) {
            throw new IllegalArgumentException("Region not found with ID: " + dto.getRegionId());
        }
    }

    @Override
    public Scheme createScheme(SchemeDTO dto) {
        validateSchemeDTO(dto);
        Scheme scheme = Scheme.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .minIncome(dto.getMinIncome())
                .maxIncome(dto.getMaxIncome())
                .minLandSize(dto.getMinLandSize())
                .categoryAllowed(dto.getCategoryAllowed())
                .grantAmountMin(dto.getGrantAmountMin())
                .grantAmountMax(dto.getGrantAmountMax())
                .regionId(dto.getRegionId())
                .status(dto.getStatus())
                .createdBy(dto.getCreatedBy())
                .build();
        Scheme saved = schemeRepository.save(scheme);
        auditLogService.log("CREATE_SCHEME", "Scheme", saved.getId(), "Created scheme: " + saved.getName());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Scheme getSchemeById(Long id) {
        return schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Scheme> getAllSchemes() {
        return schemeRepository.findAll();
    }

    @Override
    public Scheme updateScheme(Long id, SchemeDTO dto) {
        validateSchemeDTO(dto);
        Scheme scheme = getSchemeById(id);
        
        scheme.setName(dto.getName());
        scheme.setDescription(dto.getDescription());
        scheme.setMinIncome(dto.getMinIncome());
        scheme.setMaxIncome(dto.getMaxIncome());
        scheme.setMinLandSize(dto.getMinLandSize());
        scheme.setCategoryAllowed(dto.getCategoryAllowed());
        scheme.setGrantAmountMin(dto.getGrantAmountMin());
        scheme.setGrantAmountMax(dto.getGrantAmountMax());
        scheme.setRegionId(dto.getRegionId());
        scheme.setStatus(dto.getStatus());
        if (dto.getCreatedBy() != null) {
            scheme.setCreatedBy(dto.getCreatedBy());
        }

        Scheme saved = schemeRepository.save(scheme);
        auditLogService.log("UPDATE_SCHEME", "Scheme", saved.getId(), "Updated scheme: " + saved.getName());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public SchemeCriteriaDTO getSchemeCriteria(Long id) {
        Scheme scheme = getSchemeById(id);
        return SchemeCriteriaDTO.builder()
                .schemeId(scheme.getId())
                .minIncome(scheme.getMinIncome())
                .maxIncome(scheme.getMaxIncome())
                .minLandSize(scheme.getMinLandSize())
                .categoryAllowed(scheme.getCategoryAllowed())
                .grantAmountMin(scheme.getGrantAmountMin())
                .grantAmountMax(scheme.getGrantAmountMax())
                .build();
    }
}
