package com.shreespark.pos_api.payment.mapper;

import com.shreespark.pos_api.payment.dto.response.PaymentTransactionResponse;
import com.shreespark.pos_api.payment.entity.PaymentTransaction;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentTransactionResponse toResponse(PaymentTransaction t) {
        return new PaymentTransactionResponse(
                t.getId(),
                t.getSaleId(),
                t.getCustomerId(),
                t.getMethod().name(),
                t.getStatus().name(),
                t.getAmount(),
                t.getReferenceNumber(),
                t.getNote(),
                t.getCreatedAt()
        );
    }
}
