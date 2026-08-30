package com.training.module2_workflow.service;

import com.training.common.entity.Application;
import com.training.common.entity.VerificationLog;
import com.training.common.enums.AppStatus;
import com.training.module2_workflow.dto.ApplicationSubmissionDTO;
import com.training.module2_workflow.dto.VerificationDecisionDTO;

import java.util.List;

public interface ApplicationService {
    Application submitApplication(ApplicationSubmissionDTO dto);

    Application getApplicationById(Long id);

    List<Application> getQueueByStatus(AppStatus status);

    Application transitionStatus(Long applicationId, VerificationDecisionDTO decisionDto);

    List<VerificationLog> getVerificationHistory(Long applicationId);
}
