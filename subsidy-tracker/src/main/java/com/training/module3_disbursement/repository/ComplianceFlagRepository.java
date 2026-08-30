package com.training.module3_disbursement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.training.common.entity.ComplianceFlag;

public interface ComplianceFlagRepository extends JpaRepository<ComplianceFlag, Long>{
    List<ComplianceFlag> findByApplicationId(Long applicationId);
    List<ComplianceFlag> findByResolvedFalse();
}