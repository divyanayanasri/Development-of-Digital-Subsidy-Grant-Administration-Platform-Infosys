package com.training.module4_analytics.controller;

import com.training.module4_analytics.service.ReportExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE_APPROVER')")
public class ReportController {

    @Autowired
    private ReportExportService reportExportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam("format") String format) {
        try {
            if ("pdf".equalsIgnoreCase(format)) {
                byte[] data = reportExportService.exportToPdf();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"analytics_report.pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(data);
            } else if ("excel".equalsIgnoreCase(format)) {
                byte[] data = reportExportService.exportToExcel();
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"analytics_report.xlsx\"")
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(data);
            } else {
                return ResponseEntity.badRequest().body("Invalid format. Use 'pdf' or 'excel'.".getBytes());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error generating report: " + e.getMessage()).getBytes());
        }
    }
}
