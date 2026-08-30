package com.training.module2_workflow.repository;

import com.training.common.entity.Application;
import com.training.common.enums.AppStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByBeneficiaryId(Long beneficiaryId);

    // Added for Module 2 (officer review queues)
    List<Application> findByStatus(AppStatus status);

    org.springframework.data.domain.Page<Application> findByStatus(AppStatus status, org.springframework.data.domain.Pageable pageable);

    List<Application> findByAssignedOfficerId(Long assignedOfficerId);

    org.springframework.data.domain.Page<Application> findByAssignedOfficerId(Long assignedOfficerId, org.springframework.data.domain.Pageable pageable);
}
