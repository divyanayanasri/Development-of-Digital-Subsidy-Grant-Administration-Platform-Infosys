package com.training.module4_analytics.service;

import com.training.common.entity.*;
import com.training.common.enums.*;
import com.training.module1_masterdata.repository.*;
import com.training.module2_workflow.repository.*;
import com.training.module3_disbursement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private SchemeRepository schemeRepository;
    @Autowired
    private BeneficiaryRepository beneficiaryRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private VerificationLogRepository verificationLogRepository;
    @Autowired
    private DisbursementPlanRepository planRepository;
    @Autowired
    private DisbursementStageRepository stageRepository;
    @Autowired
    private ComplianceFlagRepository complianceRepository;
    @Autowired
    private com.training.module4_analytics.repository.AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (regionRepository.count() == 0) {
            seedData();
        }
    }

    @Transactional
    public void seedData() {
        // Clear first to prevent conflicts if called manually
        auditLogRepository.deleteAll();
        complianceRepository.deleteAll();
        stageRepository.deleteAll();
        planRepository.deleteAll();
        verificationLogRepository.deleteAll();
        applicationRepository.deleteAll();
        beneficiaryRepository.deleteAll();
        schemeRepository.deleteAll();
        userRepository.deleteAll();
        regionRepository.deleteAll();

        // 1. Regions
        Region north = regionRepository.save(Region.builder().name("North Region").budgetCap(new BigDecimal("5000000.00")).budgetUsed(new BigDecimal("120000.00")).build());
        Region south = regionRepository.save(Region.builder().name("South Region").budgetCap(new BigDecimal("8000000.00")).budgetUsed(new BigDecimal("350000.00")).build());
        Region west = regionRepository.save(Region.builder().name("West Region").budgetCap(new BigDecimal("3000000.00")).budgetUsed(new BigDecimal("0.00")).build());

        // 2. Users (Officers & Admin)
        User admin = userRepository.save(User.builder().name("Admin User").email("admin@gov.in").passwordHash("admin123").role(Role.ADMIN).createdAt(LocalDateTime.now()).build());
        User fo = userRepository.save(User.builder().name("Field Officer Raj").email("officer@gov.in").passwordHash("officer123").role(Role.FIELD_OFFICER).regionId(north.getId()).createdAt(LocalDateTime.now()).build());
        User doUser = userRepository.save(User.builder().name("District Officer Priya").email("do@gov.in").passwordHash("do123").role(Role.DISTRICT_OFFICER).regionId(north.getId()).createdAt(LocalDateTime.now()).build());
        User fin = userRepository.save(User.builder().name("Finance Officer Amit").email("finance@gov.in").passwordHash("finance123").role(Role.FINANCE_APPROVER).createdAt(LocalDateTime.now()).build());

        // 3. Schemes
        Scheme solar = schemeRepository.save(Scheme.builder()
                .name("Solar Pump Subsidy 2026")
                .description("Provides subsidy for installing solar water pumps for farmers")
                .minIncome(new BigDecimal("30000.00"))
                .maxIncome(new BigDecimal("300000.00"))
                .minLandSize(1.5)
                .categoryAllowed("General,OBC,SC,ST")
                .grantAmountMin(new BigDecimal("30000.00"))
                .grantAmountMax(new BigDecimal("100000.00"))
                .regionId(north.getId())
                .status(SchemeStatus.ACTIVE)
                .createdBy(admin.getId())
                .build());

        Scheme business = schemeRepository.save(Scheme.builder()
                .name("Small Business Grant")
                .description("Empower rural small-scale enterprises")
                .minIncome(new BigDecimal("10000.00"))
                .maxIncome(new BigDecimal("150000.00"))
                .minLandSize(0.0)
                .categoryAllowed("SC,ST,OBC")
                .grantAmountMin(new BigDecimal("10000.00"))
                .grantAmountMax(new BigDecimal("45000.00"))
                .regionId(south.getId())
                .status(SchemeStatus.ACTIVE)
                .createdBy(admin.getId())
                .build());

        // 4. Beneficiary Users
        User benUser1 = userRepository.save(User.builder().name("Ramesh Kumar").email("beneficiary@gov.in").passwordHash("password123").role(Role.BENEFICIARY).regionId(north.getId()).createdAt(LocalDateTime.now()).build());
        Beneficiary ben1 = beneficiaryRepository.save(Beneficiary.builder().userId(benUser1.getId()).aadharNo("111122223333").landSize(2.5).annualIncome(new BigDecimal("120000.00")).category("OBC").address("Village Khas, Ward 3, North").build());


        User benUser2 = userRepository.save(User.builder().name("Sita Devi").email("sita@gmail.com").passwordHash("password").role(Role.BENEFICIARY).regionId(south.getId()).createdAt(LocalDateTime.now()).build());
        Beneficiary ben2 = beneficiaryRepository.save(Beneficiary.builder().userId(benUser2.getId()).aadharNo("444455556666").landSize(0.5).annualIncome(new BigDecimal("80000.00")).category("SC").address("Tola B, South Region").build());

        User benUser3 = userRepository.save(User.builder().name("Vijay Singh").email("vijay@gmail.com").passwordHash("password").role(Role.BENEFICIARY).regionId(north.getId()).createdAt(LocalDateTime.now()).build());
        Beneficiary ben3 = beneficiaryRepository.save(Beneficiary.builder().userId(benUser3.getId()).aadharNo("777788889999").landSize(4.0).annualIncome(new BigDecimal("220000.00")).category("General").address("Gali 5, Sector 2, North").build());

        // 5. Applications
        // App 1: Ramesh -> Solar Pump -> APPROVED -> Plan created -> Stage 1 & 2 Released
        LocalDateTime submitted1 = LocalDateTime.now().minusDays(10);
        LocalDateTime decided1 = LocalDateTime.now().minusDays(8);
        Application app1 = applicationRepository.save(Application.builder()
                .beneficiaryId(ben1.getId())
                .schemeId(solar.getId())
                .eligibilityScore(85)
                .status(AppStatus.APPROVED)
                .routeType(RouteType.FAST_TRACK)
                .assignedOfficerId(fin.getId())
                .submittedAt(submitted1)
                .decidedAt(decided1)
                .build());

        // Logs for App 1
        verificationLogRepository.save(VerificationLog.builder().applicationId(app1.getId()).officerId(fo.getId()).role(Role.FIELD_OFFICER).decision("RECOMMEND").remarks("Verified pump location.").createdAt(submitted1.plusDays(1)).build());
        verificationLogRepository.save(VerificationLog.builder().applicationId(app1.getId()).officerId(fin.getId()).role(Role.FINANCE_APPROVER).decision("APPROVE").remarks("Budget checked and approved.").createdAt(decided1).build());

        // Plan and Stages for App 1 (Total grant: 80,000 INR)
        BigDecimal total1 = new BigDecimal("80000.00");
        DisbursementPlan plan1 = planRepository.save(DisbursementPlan.builder().applicationId(app1.getId()).totalAmount(total1).createdAt(decided1).build());

        // Stage 1 (25% = 20,000) - Released
        stageRepository.save(DisbursementStage.builder().planId(plan1.getId()).stageNo(1).milestoneName("Documentation Complete").percentage(25.0).amount(new BigDecimal("20000.00")).dueDate(LocalDate.now().minusDays(7)).status(StageStatus.RELEASED).releasedAt(decided1.plusDays(1)).build());
        // Stage 2 (35% = 28,000) - Released
        stageRepository.save(DisbursementStage.builder().planId(plan1.getId()).stageNo(2).milestoneName("Ground Verification").percentage(35.0).amount(new BigDecimal("28000.00")).dueDate(LocalDate.now().minusDays(5)).status(StageStatus.RELEASED).releasedAt(decided1.plusDays(3)).build());
        // Stage 3 (30% = 24,000) - Pending
        stageRepository.save(DisbursementStage.builder().planId(plan1.getId()).stageNo(3).milestoneName("Utilization Proof").percentage(30.0).amount(new BigDecimal("24000.00")).dueDate(LocalDate.now().plusDays(5)).status(StageStatus.PENDING).build());
        // Stage 4 (10% = 8,000) - Pending
        stageRepository.save(DisbursementStage.builder().planId(plan1.getId()).stageNo(4).milestoneName("Project Closure").percentage(10.0).amount(new BigDecimal("8000.00")).dueDate(LocalDate.now().plusDays(15)).status(StageStatus.PENDING).build());

        // App 2: Sita -> Small Business -> APPROVED -> Plan -> Stage 1 Released, Stage 2 PENDING but MISSED due date (raise Compliance Flag!)
        LocalDateTime submitted2 = LocalDateTime.now().minusDays(15);
        LocalDateTime decided2 = LocalDateTime.now().minusDays(12);
        Application app2 = applicationRepository.save(Application.builder()
                .beneficiaryId(ben2.getId())
                .schemeId(business.getId())
                .eligibilityScore(75)
                .status(AppStatus.APPROVED)
                .routeType(RouteType.ESCALATED)
                .assignedOfficerId(fin.getId())
                .submittedAt(submitted2)
                .decidedAt(decided2)
                .build());

        verificationLogRepository.save(VerificationLog.builder().applicationId(app2.getId()).officerId(fo.getId()).role(Role.FIELD_OFFICER).decision("RECOMMEND").remarks("Shop exists.").createdAt(submitted2.plusDays(1)).build());
        verificationLogRepository.save(VerificationLog.builder().applicationId(app2.getId()).officerId(doUser.getId()).role(Role.DISTRICT_OFFICER).decision("RECOMMEND").remarks("Approved at district level.").createdAt(submitted2.plusDays(2)).build());
        verificationLogRepository.save(VerificationLog.builder().applicationId(app2.getId()).officerId(fin.getId()).role(Role.FINANCE_APPROVER).decision("APPROVE").remarks("Approved").createdAt(decided2).build());

        BigDecimal total2 = new BigDecimal("40000.00");
        DisbursementPlan plan2 = planRepository.save(DisbursementPlan.builder().applicationId(app2.getId()).totalAmount(total2).createdAt(decided2).build());

        // Stage 1 (25% = 10,000) - Released
        stageRepository.save(DisbursementStage.builder().planId(plan2.getId()).stageNo(1).milestoneName("Documentation Complete").percentage(25.0).amount(new BigDecimal("10000.00")).dueDate(LocalDate.now().minusDays(10)).status(StageStatus.RELEASED).releasedAt(decided2.plusDays(1)).build());
        // Stage 2 (35% = 14,000) - Missed
        DisbursementStage s2_2 = stageRepository.save(DisbursementStage.builder().planId(plan2.getId()).stageNo(2).milestoneName("Ground Verification").percentage(35.0).amount(new BigDecimal("14000.00")).dueDate(LocalDate.now().minusDays(2)).status(StageStatus.MISSED).build());

        // Raise compliance flag for Sita (App 2)
        complianceRepository.save(ComplianceFlag.builder().applicationId(app2.getId()).stageId(s2_2.getId()).flagType("MISSED_DEADLINE").raisedAt(LocalDateTime.now().minusDays(2)).resolved(false).build());

        // App 3: Vijay -> Solar Pump -> REJECTED
        LocalDateTime submitted3 = LocalDateTime.now().minusDays(5);
        LocalDateTime decided3 = LocalDateTime.now().minusDays(3);
        Application app3 = applicationRepository.save(Application.builder()
                .beneficiaryId(ben3.getId())
                .schemeId(solar.getId())
                .eligibilityScore(45)
                .status(AppStatus.REJECTED)
                .routeType(RouteType.ESCALATED)
                .assignedOfficerId(doUser.getId())
                .submittedAt(submitted3)
                .decidedAt(decided3)
                .build());

        verificationLogRepository.save(VerificationLog.builder().applicationId(app3.getId()).officerId(fo.getId()).role(Role.FIELD_OFFICER).decision("REJECT").remarks("Land ownership papers missing.").createdAt(submitted3.plusDays(1)).build());
        verificationLogRepository.save(VerificationLog.builder().applicationId(app3.getId()).officerId(doUser.getId()).role(Role.DISTRICT_OFFICER).decision("REJECT").remarks("Low eligibility score and field rejection.").createdAt(decided3).build());

        // Seeding Audit Logs
        auditLogRepository.save(com.training.common.entity.AuditLog.builder().userId(admin.getId()).action("CREATE_SCHEME").entityType("Scheme").entityId(solar.getId()).timestamp(LocalDateTime.now().minusDays(10)).details("Created Solar Pump Subsidy 2026").build());
        auditLogRepository.save(com.training.common.entity.AuditLog.builder().userId(admin.getId()).action("CREATE_SCHEME").entityType("Scheme").entityId(business.getId()).timestamp(LocalDateTime.now().minusDays(9)).details("Created Small Business Grant").build());
        auditLogRepository.save(com.training.common.entity.AuditLog.builder().userId(benUser1.getId()).action("SUBMIT_APPLICATION").entityType("Application").entityId(app1.getId()).timestamp(submitted1).details("Submitted application for Solar Pump Subsidy").build());
        auditLogRepository.save(com.training.common.entity.AuditLog.builder().userId(fin.getId()).action("APPROVE_APPLICATION").entityType("Application").entityId(app1.getId()).timestamp(decided1).details("Approved application & simulated disbursement").build());
    }
}
