package com.shreespark.pos_api.sales.mapper;

import com.shreespark.pos_api.sales.dto.response.SaleResponse;
import com.shreespark.pos_api.sales.entity.Sale;
import com.shreespark.pos_api.sales.entity.SaleItem;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

    public SaleResponse toResponse(Sale s) {
        return new SaleResponse(
                s.getId(),
                s.getInvoiceNumber(),
                s.getCustomerId(),
                s.getCustomerName(),
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
                s.getItems().stream().map(this::toItemResponse).toList(),
                s.getCreatedAt()
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
