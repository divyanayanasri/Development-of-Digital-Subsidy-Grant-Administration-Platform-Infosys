package com.training.module4_analytics.service.impl;

import com.training.common.entity.*;
import com.training.common.enums.StageStatus;
import com.training.module1_masterdata.repository.*;
import com.training.module2_workflow.repository.ApplicationRepository;
import com.training.module3_disbursement.repository.*;
import com.training.module4_analytics.dto.*;
import com.training.module4_analytics.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private SchemeRepository schemeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DisbursementPlanRepository planRepository;

    @Autowired
    private DisbursementStageRepository stageRepository;

    @Autowired
    private ComplianceFlagRepository complianceRepository;

    @Override
    public List<FundUtilizationDTO> getFundUtilization() {
        List<Scheme> schemes = schemeRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<DisbursementPlan> plans = planRepository.findAll();
        List<DisbursementStage> stages = stageRepository.findAll();

        Map<Long, List<DisbursementPlan>> plansByAppId = plans.stream()
                .collect(Collectors.groupingBy(DisbursementPlan::getApplicationId));

        Map<Long, List<DisbursementStage>> stagesByPlanId = stages.stream()
                .collect(Collectors.groupingBy(DisbursementStage::getPlanId));

        List<FundUtilizationDTO> result = new ArrayList<>();
        for (Scheme scheme : schemes) {
            BigDecimal allocated = BigDecimal.ZERO;
            BigDecimal released = BigDecimal.ZERO;

            for (Application app : applications) {
                if (app.getSchemeId().equals(scheme.getId())) {
                    List<DisbursementPlan> appPlans = plansByAppId.getOrDefault(app.getId(), Collections.emptyList());
                    for (DisbursementPlan plan : appPlans) {
                        if (plan.getTotalAmount() != null) {
                            allocated = allocated.add(plan.getTotalAmount());
                        }
                        List<DisbursementStage> planStages = stagesByPlanId.getOrDefault(plan.getId(), Collections.emptyList());
                        for (DisbursementStage stage : planStages) {
                            if (stage.getStatus() == StageStatus.RELEASED && stage.getAmount() != null) {
                                released = released.add(stage.getAmount());
                            }
                        }
                    }
                }
            }
            result.add(FundUtilizationDTO.builder()
                    .schemeId(scheme.getId())
                    .schemeName(scheme.getName())
                    .allocatedAmount(allocated)
                    .releasedAmount(released)
                    .build());
        }
        return result;
    }

    @Override
    public List<BudgetExhaustionDTO> getBudgetExhaustion() {
        List<Region> regions = regionRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Beneficiary> beneficiaries = beneficiaryRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<DisbursementPlan> plans = planRepository.findAll();
        List<DisbursementStage> stages = stageRepository.findAll();

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        Map<Long, Beneficiary> benMap = beneficiaries.stream()
                .collect(Collectors.toMap(Beneficiary::getId, Function.identity(), (a, b) -> a));

        Map<Long, Application> appMap = applications.stream()
                .collect(Collectors.toMap(Application::getId, Function.identity(), (a, b) -> a));

        Map<Long, DisbursementPlan> planMap = plans.stream()
                .collect(Collectors.toMap(DisbursementPlan::getId, Function.identity(), (a, b) -> a));

        // Group released amounts by region ID
        Map<Long, BigDecimal> releasedByRegion = new HashMap<>();
        for (DisbursementStage stage : stages) {
            if (stage.getStatus() == StageStatus.RELEASED && stage.getAmount() != null) {
                DisbursementPlan plan = planMap.get(stage.getPlanId());
                if (plan != null) {
                    Application app = appMap.get(plan.getApplicationId());
                    if (app != null) {
                        Beneficiary ben = benMap.get(app.getBeneficiaryId());
                        if (ben != null) {
                            User usr = userMap.get(ben.getUserId());
                            if (usr != null && usr.getRegionId() != null) {
                                Long regId = usr.getRegionId();
                                releasedByRegion.put(regId, releasedByRegion.getOrDefault(regId, BigDecimal.ZERO).add(stage.getAmount()));
                            }
                        }
                    }
                }
            }
        }

        List<BudgetExhaustionDTO> result = new ArrayList<>();
        for (Region region : regions) {
            BigDecimal budgetCap = region.getBudgetCap() != null ? region.getBudgetCap() : BigDecimal.ZERO;
            BigDecimal budgetUsed = region.getBudgetUsed() != null ? region.getBudgetUsed() : BigDecimal.ZERO;
            BigDecimal calculatedReleased = releasedByRegion.getOrDefault(region.getId(), BigDecimal.ZERO);

            double exhaustionPercentage = 0.0;
            if (budgetCap.compareTo(BigDecimal.ZERO) > 0) {
                exhaustionPercentage = calculatedReleased.multiply(new BigDecimal("100"))
                        .divide(budgetCap, 2, java.math.RoundingMode.HALF_UP)
                        .doubleValue();
            }

            result.add(BudgetExhaustionDTO.builder()
                    .regionId(region.getId())
                    .regionName(region.getName())
                    .budgetCap(budgetCap)
                    .budgetUsed(budgetUsed)
                    .calculatedReleased(calculatedReleased)
                    .exhaustionPercentage(exhaustionPercentage)
                    .build());
        }
        return result;
    }

    @Override
    public List<NonComplianceDTO> getNonComplianceSummary() {
        List<Scheme> schemes = schemeRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<ComplianceFlag> flags = complianceRepository.findAll();

        Map<Long, Application> appMap = applications.stream()
                .collect(Collectors.toMap(Application::getId, Function.identity(), (a, b) -> a));

        Map<Long, Long> nonComplianceCountByScheme = new HashMap<>();
        for (ComplianceFlag flag : flags) {
            if (!flag.isResolved()) {
                Application app = appMap.get(flag.getApplicationId());
                if (app != null) {
                    Long schemeId = app.getSchemeId();
                    nonComplianceCountByScheme.put(schemeId, nonComplianceCountByScheme.getOrDefault(schemeId, 0L) + 1);
                }
            }
        }

        List<NonComplianceDTO> result = new ArrayList<>();
        for (Scheme scheme : schemes) {
            Long count = nonComplianceCountByScheme.getOrDefault(scheme.getId(), 0L);
            result.add(NonComplianceDTO.builder()
                    .schemeId(scheme.getId())
                    .schemeName(scheme.getName())
                    .nonComplianceCount(count)
                    .build());
        }
        return result;
    }

    @Override
    public List<TurnaroundTimeDTO> getTurnaroundTimes() {
        List<Application> applications = applicationRepository.findAll();

        Map<String, List<Double>> hoursByStatus = new HashMap<>();
        for (Application app : applications) {
            if (app.getDecidedAt() != null && app.getSubmittedAt() != null) {
                String statusStr = app.getStatus().name();
                java.time.Duration duration = java.time.Duration.between(app.getSubmittedAt(), app.getDecidedAt());
                double hours = duration.toMinutes() / 60.0;
                hoursByStatus.computeIfAbsent(statusStr, k -> new ArrayList<>()).add(hours);
            }
        }

        List<TurnaroundTimeDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : hoursByStatus.entrySet()) {
            List<Double> hoursList = entry.getValue();
            double avgHours = hoursList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            // Format to 2 decimal places
            avgHours = BigDecimal.valueOf(avgHours).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();

            result.add(TurnaroundTimeDTO.builder()
                    .status(entry.getKey())
                    .averageTurnaroundTimeInHours(avgHours)
                    .applicationCount((long) hoursList.size())
                    .build());
        }
        return result;
    }
}
