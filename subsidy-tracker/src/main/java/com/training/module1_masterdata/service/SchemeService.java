package com.training.module1_masterdata.service;

import com.training.common.entity.Scheme;
import com.training.common.dto.SchemeCriteriaDTO;
import com.training.module1_masterdata.dto.SchemeDTO;
import java.util.List;

public interface SchemeService {
    Scheme createScheme(SchemeDTO dto);
    Scheme getSchemeById(Long id);
    List<Scheme> getAllSchemes();
    Scheme updateScheme(Long id, SchemeDTO dto);
    SchemeCriteriaDTO getSchemeCriteria(Long id);
}
