package com.shreespark.pos_api.sales.mapper;

import com.shreespark.pos_api.sales.dto.response.SaleAuditLogResponse;
import com.shreespark.pos_api.sales.dto.response.SaleResponse;
import com.shreespark.pos_api.sales.entity.Sale;
import com.shreespark.pos_api.sales.entity.SaleAuditLog;
import com.shreespark.pos_api.sales.entity.SaleItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SaleMapper {

    public SaleResponse toResponse(Sale s) {
        return toResponse(s, Collections.emptyList());
    }

    public SaleResponse toResponse(Sale s, List<SaleAuditLog> auditLogs) {
        return new SaleResponse(
                s.getId(),
                s.getInvoiceNumber(),
                s.getCustomerId(),
                s.getCustomerName(),
                s.getStaffId(),
                s.getStaffName(),
                s.getEditedByStaffId(),
                s.getEditedByStaffName(),
                s.getStatus().name(),
                s.getPaymentMethod().name(),
                s.getSubtotal(),
                s.getDiscountAmount(),
                s.getTaxableAmount(),
                s.getCgstAmount(),
                s.getSgstAmount(),
                s.getIgstAmount(),
                s.getTotalAmount(),
                s.getPaidAmount(),
                s.getKhataAmount(),
                s.isInterState(),
                s.getNote(),
                s.getItems() != null ? s.getItems().stream().map(this::toItemResponse).toList() : Collections.emptyList(),
                auditLogs != null ? auditLogs.stream().map(this::toAuditLogResponse).toList() : Collections.emptyList(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }

    public SaleAuditLogResponse toAuditLogResponse(SaleAuditLog log) {
        return new SaleAuditLogResponse(
                log.getId(),
                log.getSaleId(),
                log.getAction(),
                log.getStaffId(),
                log.getStaffName(),
                log.getDetails(),
                log.getCreatedAt()
        );
    }

    private SaleResponse.SaleItemResponse toItemResponse(SaleItem i) {
        return new SaleResponse.SaleItemResponse(
                i.getId(),
                i.getProductId(),
                i.getProductName(),
                i.getHsnCode(),
                i.getQuantity(),
                i.getUnitPrice(),
                i.getDiscountPercent(),
                i.getDiscountAmount(),
                i.getTaxableAmount(),
                i.getCgstPercent(),
                i.getCgstAmount(),
                i.getSgstPercent(),
                i.getSgstAmount(),
                i.getIgstPercent(),
                i.getIgstAmount(),
                i.getLineTotal()
        );
    }
}
