package com.shreespark.pos_api.sales.service;

import com.shreespark.pos_api.sales.dto.request.CreateSaleRequest;
import com.shreespark.pos_api.sales.dto.request.UpdateSaleRequest;
import com.shreespark.pos_api.sales.dto.response.SaleAuditLogResponse;
import com.shreespark.pos_api.sales.dto.response.SaleResponse;

import java.util.List;
import java.util.UUID;

public interface SaleService {
    SaleResponse create(UUID tenantId, UUID staffId, CreateSaleRequest request);
    SaleResponse getById(UUID tenantId, UUID saleId);
    SaleResponse getByInvoiceNumber(UUID tenantId, String invoiceNumber);
    List<SaleResponse> getAll(UUID tenantId);
    List<SaleResponse> getByCustomer(UUID tenantId, UUID customerId);
    SaleResponse update(UUID tenantId, UUID staffId, UUID saleId, UpdateSaleRequest request);
    SaleResponse cancel(UUID tenantId, UUID saleId);
    SaleResponse cancel(UUID tenantId, UUID staffId, UUID saleId);
    SaleResponse logPrint(UUID tenantId, UUID staffId, UUID saleId, String printType);
    List<SaleAuditLogResponse> getAllAuditLogs(UUID tenantId);
}
