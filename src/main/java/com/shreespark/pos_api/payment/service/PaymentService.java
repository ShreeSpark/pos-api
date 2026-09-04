package com.shreespark.pos_api.payment.service;

import com.shreespark.pos_api.common.enums.PaymentMethod;
import com.shreespark.pos_api.payment.dto.request.UpiWebhookRequest;
import com.shreespark.pos_api.payment.dto.response.PaymentTransactionResponse;
import com.shreespark.pos_api.payment.entity.PaymentTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentTransaction record(UUID tenantId, UUID saleId, UUID customerId,
                              PaymentMethod method, BigDecimal amount, String referenceNumber);
    void handleUpiWebhook(UUID tenantId, UpiWebhookRequest request);
    List<PaymentTransactionResponse> getBySale(UUID tenantId, UUID saleId);
    List<PaymentTransactionResponse> getByCustomer(UUID tenantId, UUID customerId);
}
