package com.training.module1_masterdata.repository;

import com.training.common.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByUserId(Long userId);
    boolean existsByAadharNo(String aadharNo);
}
