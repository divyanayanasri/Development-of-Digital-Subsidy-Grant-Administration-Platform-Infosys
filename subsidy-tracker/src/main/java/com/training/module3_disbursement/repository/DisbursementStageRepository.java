package com.training.module3_disbursement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.common.entity.DisbursementStage;
import com.training.common.enums.StageStatus;

public interface DisbursementStageRepository extends JpaRepository<DisbursementStage, Long> {

    List<DisbursementStage> findByPlanId(Long planId);

    List<DisbursementStage> findByStatus(StageStatus status);

    List<DisbursementStage> findByDueDateBefore(LocalDate date);

}