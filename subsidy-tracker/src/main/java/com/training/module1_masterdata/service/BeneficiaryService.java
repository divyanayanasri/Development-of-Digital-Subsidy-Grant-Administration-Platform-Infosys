package com.training.module1_masterdata.service;

import com.training.common.entity.Beneficiary;
import com.training.common.entity.Application;
import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.module1_masterdata.dto.BeneficiaryRegistrationDTO;
import java.util.List;

public interface BeneficiaryService {
    Beneficiary registerBeneficiary(BeneficiaryRegistrationDTO dto);
    Beneficiary getBeneficiaryById(Long id);
    BeneficiaryEligibilityDTO getBeneficiaryEligibility(Long id);
    List<Application> getApplicationsByBeneficiaryId(Long id);
}
