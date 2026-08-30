package com.training.module4_analytics.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.training.module4_analytics.dto.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    @Autowired
    private AnalyticsService analyticsService;

    public byte[] exportToExcel() throws IOException {
        List<FundUtilizationDTO> fundUtilization = analyticsService.getFundUtilization();
        List<BudgetExhaustionDTO> budgetExhaustion = analyticsService.getBudgetExhaustion();
        List<NonComplianceDTO> nonCompliance = analyticsService.getNonComplianceSummary();
        List<TurnaroundTimeDTO> turnaroundTimes = analyticsService.getTurnaroundTimes();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Style definitions
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Sheet 1: Fund Utilization
            Sheet sheet1 = workbook.createSheet("Fund Utilization");
            Row h1 = sheet1.createRow(0);
            String[] headers1 = {"Scheme ID", "Scheme Name", "Allocated Amount (INR)", "Released Amount (INR)"};
            for (int i = 0; i < headers1.length; i++) {
                Cell cell = h1.createCell(i);
                cell.setCellValue(headers1[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx1 = 1;
            for (FundUtilizationDTO dto : fundUtilization) {
                Row row = sheet1.createRow(rowIdx1++);
                row.createCell(0).setCellValue(dto.getSchemeId());
                row.createCell(1).setCellValue(dto.getSchemeName());
                row.createCell(2).setCellValue(dto.getAllocatedAmount().doubleValue());
                row.createCell(3).setCellValue(dto.getReleasedAmount().doubleValue());
            }
            for (int i = 0; i < headers1.length; i++) {
                sheet1.autoSizeColumn(i);
            }

            // Sheet 2: Budget Exhaustion
            Sheet sheet2 = workbook.createSheet("Budget Exhaustion");
            Row h2 = sheet2.createRow(0);
            String[] headers2 = {"Region ID", "Region Name", "Budget Cap (INR)", "Budget Used (INR)", "Calculated Released (INR)", "Exhaustion %"};
            for (int i = 0; i < headers2.length; i++) {
                Cell cell = h2.createCell(i);
                cell.setCellValue(headers2[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx2 = 1;
            for (BudgetExhaustionDTO dto : budgetExhaustion) {
                Row row = sheet2.createRow(rowIdx2++);
                row.createCell(0).setCellValue(dto.getRegionId());
                row.createCell(1).setCellValue(dto.getRegionName());
                row.createCell(2).setCellValue(dto.getBudgetCap().doubleValue());
                row.createCell(3).setCellValue(dto.getBudgetUsed().doubleValue());
                row.createCell(4).setCellValue(dto.getCalculatedReleased().doubleValue());
                row.createCell(5).setCellValue(dto.getExhaustionPercentage());
            }
            for (int i = 0; i < headers2.length; i++) {
                sheet2.autoSizeColumn(i);
            }

            // Sheet 3: Non-Compliance Summary
            Sheet sheet3 = workbook.createSheet("Non-Compliance Summary");
            Row h3 = sheet3.createRow(0);
            String[] headers3 = {"Scheme ID", "Scheme Name", "Unresolved Compliance Flags"};
            for (int i = 0; i < headers3.length; i++) {
                Cell cell = h3.createCell(i);
                cell.setCellValue(headers3[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx3 = 1;
            for (NonComplianceDTO dto : nonCompliance) {
                Row row = sheet3.createRow(rowIdx3++);
                row.createCell(0).setCellValue(dto.getSchemeId());
                row.createCell(1).setCellValue(dto.getSchemeName());
                row.createCell(2).setCellValue(dto.getNonComplianceCount());
            }
            for (int i = 0; i < headers3.length; i++) {
                sheet3.autoSizeColumn(i);
            }

            // Sheet 4: Turnaround Times
            Sheet sheet4 = workbook.createSheet("Turnaround Times");
            Row h4 = sheet4.createRow(0);
            String[] headers4 = {"Application Status", "Avg Turnaround Time (Hours)", "Application Count"};
            for (int i = 0; i < headers4.length; i++) {
                Cell cell = h4.createCell(i);
                cell.setCellValue(headers4[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowIdx4 = 1;
            for (TurnaroundTimeDTO dto : turnaroundTimes) {
                Row row = sheet4.createRow(rowIdx4++);
                row.createCell(0).setCellValue(dto.getStatus());
                row.createCell(1).setCellValue(dto.getAverageTurnaroundTimeInHours());
                row.createCell(2).setCellValue(dto.getApplicationCount());
            }
            for (int i = 0; i < headers4.length; i++) {
                sheet4.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] exportToPdf() throws DocumentException {
        List<FundUtilizationDTO> fundUtilization = analyticsService.getFundUtilization();
        List<BudgetExhaustionDTO> budgetExhaustion = analyticsService.getBudgetExhaustion();
        List<NonComplianceDTO> nonCompliance = analyticsService.getNonComplianceSummary();
        List<TurnaroundTimeDTO> turnaroundTimes = analyticsService.getTurnaroundTimes();

        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Title and Metadata
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE);
        Font thFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font tdFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        Paragraph title = new Paragraph("Government Subsidy & Grant Disbursement Tracking System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subtitle = new Paragraph("Analytics Executive Summary Report - Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20f);
        document.add(subtitle);

        // Section 1: Fund Utilization
        document.add(new Paragraph("1. Fund Utilization by Scheme", sectionFont));
        Paragraph space = new Paragraph(" ");
        space.setSpacingAfter(5f);
        document.add(space);

        PdfPTable table1 = new PdfPTable(4);
        table1.setWidthPercentage(100);
        addTableHeader(table1, new String[]{"Scheme ID", "Scheme Name", "Allocated Amount (INR)", "Released Amount (INR)"}, thFont);
        for (FundUtilizationDTO dto : fundUtilization) {
            table1.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getSchemeId()), tdFont)));
            table1.addCell(new PdfPCell(new Phrase(dto.getSchemeName(), tdFont)));
            table1.addCell(new PdfPCell(new Phrase(dto.getAllocatedAmount().toString(), tdFont)));
            table1.addCell(new PdfPCell(new Phrase(dto.getReleasedAmount().toString(), tdFont)));
        }
        table1.setSpacingAfter(20f);
        document.add(table1);

        // Section 2: Budget Exhaustion
        document.add(new Paragraph("2. Budget Exhaustion by Region", sectionFont));
        document.add(space);

        PdfPTable table2 = new PdfPTable(6);
        table2.setWidthPercentage(100);
        addTableHeader(table2, new String[]{"Region ID", "Region Name", "Budget Cap (INR)", "Budget Used (INR)", "Calculated Released", "Exhaustion %"}, thFont);
        for (BudgetExhaustionDTO dto : budgetExhaustion) {
            table2.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getRegionId()), tdFont)));
            table2.addCell(new PdfPCell(new Phrase(dto.getRegionName(), tdFont)));
            table2.addCell(new PdfPCell(new Phrase(dto.getBudgetCap().toString(), tdFont)));
            table2.addCell(new PdfPCell(new Phrase(dto.getBudgetUsed().toString(), tdFont)));
            table2.addCell(new PdfPCell(new Phrase(dto.getCalculatedReleased().toString(), tdFont)));
            table2.addCell(new PdfPCell(new Phrase(dto.getExhaustionPercentage() + "%", tdFont)));
        }
        table2.setSpacingAfter(20f);
        document.add(table2);

        // Section 3: Non-Compliance Summary
        document.add(new Paragraph("3. Non-Compliance Flags by Scheme", sectionFont));
        document.add(space);

        PdfPTable table3 = new PdfPTable(3);
        table3.setWidthPercentage(100);
        addTableHeader(table3, new String[]{"Scheme ID", "Scheme Name", "Unresolved Flags Count"}, thFont);
        for (NonComplianceDTO dto : nonCompliance) {
            table3.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getSchemeId()), tdFont)));
            table3.addCell(new PdfPCell(new Phrase(dto.getSchemeName(), tdFont)));
            table3.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getNonComplianceCount()), tdFont)));
        }
        table3.setSpacingAfter(20f);
        document.add(table3);

        // Section 4: Turnaround Times
        document.add(new Paragraph("4. Application Decision Turnaround Times", sectionFont));
        document.add(space);

        PdfPTable table4 = new PdfPTable(3);
        table4.setWidthPercentage(100);
        addTableHeader(table4, new String[]{"Application Status", "Avg Turnaround Time (Hours)", "Decided Applications"}, thFont);
        for (TurnaroundTimeDTO dto : turnaroundTimes) {
            table4.addCell(new PdfPCell(new Phrase(dto.getStatus(), tdFont)));
            table4.addCell(new PdfPCell(new Phrase(dto.getAverageTurnaroundTimeInHours() + " hrs", tdFont)));
            table4.addCell(new PdfPCell(new Phrase(String.valueOf(dto.getApplicationCount()), tdFont)));
        }
        table4.setSpacingAfter(20f);
        document.add(table4);

        document.close();
        return baos.toByteArray();
    }

    private void addTableHeader(PdfPTable table, String[] headerTitles, Font font) {
        for (String title : headerTitles) {
            PdfPCell header = new PdfPCell(new Phrase(title, font));
            header.setBackgroundColor(new Color(0, 51, 102));
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setPadding(6);
            table.addCell(header);
        }
    }
}
