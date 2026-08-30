package com.training.module4_analytics;

import com.training.module4_analytics.service.DatabaseSeeder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AnalyticsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatabaseSeeder databaseSeeder;

    @BeforeEach
    public void setup() {
        // Explicitly seed data before each test
        databaseSeeder.seedData();
    }

    @Test
    public void testGetFundUtilization() throws Exception {
        mockMvc.perform(get("/api/analytics/fund-utilization"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.schemeName == 'Solar Pump Subsidy 2026')].allocatedAmount", contains(80000.0)))
                .andExpect(jsonPath("$[?(@.schemeName == 'Solar Pump Subsidy 2026')].releasedAmount", contains(48000.0)))
                .andExpect(jsonPath("$[?(@.schemeName == 'Small Business Grant')].allocatedAmount", contains(40000.0)))
                .andExpect(jsonPath("$[?(@.schemeName == 'Small Business Grant')].releasedAmount", contains(10000.0)));
    }

    @Test
    public void testGetBudgetExhaustion() throws Exception {
        mockMvc.perform(get("/api/analytics/budget-exhaustion"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[?(@.regionName == 'North Region')].calculatedReleased", contains(48000.0)))
                .andExpect(jsonPath("$[?(@.regionName == 'South Region')].calculatedReleased", contains(10000.0)))
                .andExpect(jsonPath("$[?(@.regionName == 'West Region')].calculatedReleased", contains(0.0)));
    }

    @Test
    public void testGetNonCompliance() throws Exception {
        mockMvc.perform(get("/api/analytics/non-compliance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.schemeName == 'Small Business Grant')].nonComplianceCount", contains(1)))
                .andExpect(jsonPath("$[?(@.schemeName == 'Solar Pump Subsidy 2026')].nonComplianceCount", contains(0)));
    }

    @Test
    public void testGetTurnaroundTimes() throws Exception {
        mockMvc.perform(get("/api/analytics/turnaround-times"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.status == 'APPROVED')].applicationCount", contains(2)))
                .andExpect(jsonPath("$[?(@.status == 'REJECTED')].applicationCount", contains(1)));
    }

    @Test
    public void testExportPdfReport() throws Exception {
        mockMvc.perform(get("/api/reports/export?format=pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(header().string("Content-Disposition", containsString("analytics_report.pdf")));
    }

    @Test
    public void testExportExcelReport() throws Exception {
        mockMvc.perform(get("/api/reports/export?format=excel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(header().string("Content-Disposition", containsString("analytics_report.xlsx")));
    }

    @Test
    public void testExportInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/reports/export?format=csv"))
                .andExpect(status().isBadRequest());
    }
}
