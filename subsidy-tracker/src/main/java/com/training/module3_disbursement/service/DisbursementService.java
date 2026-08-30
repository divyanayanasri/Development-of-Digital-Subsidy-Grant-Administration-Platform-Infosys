package com.training.module3_disbursement.service;

public interface DisbursementService {

    void createDisbursementPlan(Long applicationId);

    void configureDisbursementPlan(com.training.module3_disbursement.dto.DisbursementConfigurationDTO configDto);

    void releaseStage(Long stageId);

    void checkCompliance();
}