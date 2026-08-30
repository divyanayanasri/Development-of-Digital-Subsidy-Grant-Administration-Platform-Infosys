package com.training.module2_workflow.repository;

import com.training.common.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VerificationLogRepository extends JpaRepository<VerificationLog, Long> {
    List<VerificationLog> findByApplicationIdOrderByCreatedAtAsc(Long applicationId);
}
