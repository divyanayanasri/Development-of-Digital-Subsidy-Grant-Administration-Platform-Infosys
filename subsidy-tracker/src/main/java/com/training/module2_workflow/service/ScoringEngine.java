package com.training.module2_workflow.service;

import com.training.common.dto.BeneficiaryEligibilityDTO;
import com.training.common.dto.SchemeCriteriaDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Plain scoring service - intentionally has no controller of its own.
 * It only depends on the Module 1 DTO contract (common/dto), never on
 * Module 1's entities/repositories, so it can be built and unit tested
 * against hardcoded BeneficiaryEligibilityDTO / SchemeCriteriaDTO instances
 * before Module 1 is finished.
 *
 * Score is out of 100, split across three weighted checks:
 *  - income within the scheme's [minIncome, maxIncome] band   (40 pts)
 *  - land size meets the scheme's minLandSize                 (30 pts)
 *  - beneficiary category matches the scheme's allowed category (30 pts)
 * A criterion that the scheme did not specify is treated as satisfied.
 */
@Service
public class ScoringEngine {

    private static final int INCOME_WEIGHT = 40;
    private static final int LAND_WEIGHT = 30;
    private static final int CATEGORY_WEIGHT = 30;

    public int calculateScore(BeneficiaryEligibilityDTO eligibility, SchemeCriteriaDTO criteria) {
        if (eligibility == null || criteria == null) {
            throw new IllegalArgumentException("Eligibility and criteria data are required to compute a score");
        }

        int score = 0;
        score += scoreIncome(eligibility, criteria);
        score += scoreLandSize(eligibility, criteria);
        score += scoreCategory(eligibility, criteria);

        return Math.max(0, Math.min(100, score));
    }

    private int scoreIncome(BeneficiaryEligibilityDTO eligibility, SchemeCriteriaDTO criteria) {
        BigDecimal income = eligibility.getIncome();
        BigDecimal min = criteria.getMinIncome();
        BigDecimal max = criteria.getMaxIncome();

        if (min == null && max == null) {
            return INCOME_WEIGHT;
        }
        if (income == null) {
            return 0;
        }
        boolean aboveMin = (min == null) || income.compareTo(min) >= 0;
        boolean belowMax = (max == null) || income.compareTo(max) <= 0;
        return (aboveMin && belowMax) ? INCOME_WEIGHT : 0;
    }

    private int scoreLandSize(BeneficiaryEligibilityDTO eligibility, SchemeCriteriaDTO criteria) {
        Double minLandSize = criteria.getMinLandSize();
        if (minLandSize == null) {
            return LAND_WEIGHT;
        }
        Double landSize = eligibility.getLandSize();
        if (landSize == null) {
            return 0;
        }
        return landSize >= minLandSize ? LAND_WEIGHT : 0;
    }

    private int scoreCategory(BeneficiaryEligibilityDTO eligibility, SchemeCriteriaDTO criteria) {
        String allowed = criteria.getCategoryAllowed();
        if (allowed == null || allowed.isBlank() || allowed.equalsIgnoreCase("ALL")) {
            return CATEGORY_WEIGHT;
        }
        String category = eligibility.getCategory();
        if (category == null) {
            return 0;
        }
        return allowed.equalsIgnoreCase(category) ? CATEGORY_WEIGHT : 0;
    }
}
