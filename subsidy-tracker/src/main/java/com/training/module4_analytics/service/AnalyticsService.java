package com.training.module4_analytics.service;

import com.training.module4_analytics.dto.*;
import java.util.List;

public interface AnalyticsService {
    List<FundUtilizationDTO> getFundUtilization();
    List<BudgetExhaustionDTO> getBudgetExhaustion();
    List<NonComplianceDTO> getNonComplianceSummary();
    List<TurnaroundTimeDTO> getTurnaroundTimes();
}
