package com.shreespark.pos_api.reports.service;

import com.shreespark.pos_api.reports.dto.response.CustomerLedgerResponse;
import com.shreespark.pos_api.reports.dto.response.GstReportResponse;
import com.shreespark.pos_api.reports.dto.response.SalesReportResponse;
import com.shreespark.pos_api.reports.dto.response.StockReportResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
    SalesReportResponse salesReport(UUID tenantId, LocalDate from, LocalDate to);
    StockReportResponse stockReport(UUID tenantId);
    GstReportResponse gstReport(UUID tenantId, LocalDate from, LocalDate to);
    CustomerLedgerResponse customerLedger(UUID tenantId, UUID customerId);
}
