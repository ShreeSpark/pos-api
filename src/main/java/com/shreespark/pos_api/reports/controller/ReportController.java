package com.shreespark.pos_api.reports.controller;

import com.shreespark.pos_api.auth.service.JwtService;
import com.shreespark.pos_api.common.ApiResponse;
import com.shreespark.pos_api.reports.dto.response.CustomerLedgerResponse;
import com.shreespark.pos_api.reports.dto.response.GstReportResponse;
import com.shreespark.pos_api.reports.dto.response.SalesReportResponse;
import com.shreespark.pos_api.reports.dto.response.StockReportResponse;
import com.shreespark.pos_api.reports.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<SalesReportResponse>> salesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.salesReport(tenantId(http), from, to)));
    }

    @GetMapping("/stock")
    public ResponseEntity<ApiResponse<StockReportResponse>> stockReport(HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.stockReport(tenantId(http))));
    }

    @GetMapping("/gst")
    public ResponseEntity<ApiResponse<GstReportResponse>> gstReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.gstReport(tenantId(http), from, to)));
    }

    @GetMapping("/customers/{customerId}/ledger")
    public ResponseEntity<ApiResponse<CustomerLedgerResponse>> customerLedger(
            @PathVariable UUID customerId, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.ok(
                reportService.customerLedger(tenantId(http), customerId)));
    }

    private UUID tenantId(HttpServletRequest http) {
        return jwtService.extractTenantId(http.getHeader("Authorization").substring(7));
    }
}
