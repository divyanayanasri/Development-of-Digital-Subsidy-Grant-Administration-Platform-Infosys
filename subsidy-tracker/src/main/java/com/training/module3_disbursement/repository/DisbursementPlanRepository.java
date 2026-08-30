package com.training.module3_disbursement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.training.common.entity.DisbursementPlan;

public interface DisbursementPlanRepository extends JpaRepository<DisbursementPlan, Long> {
    java.util.Optional<DisbursementPlan> findByApplicationId(Long applicationId);
}