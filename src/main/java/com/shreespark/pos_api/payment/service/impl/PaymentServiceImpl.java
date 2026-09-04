package com.shreespark.pos_api.payment.service.impl;

import com.shreespark.pos_api.common.enums.PaymentMethod;
import com.shreespark.pos_api.common.enums.PaymentStatus;
import com.shreespark.pos_api.payment.dto.request.UpiWebhookRequest;
import com.shreespark.pos_api.payment.dto.response.PaymentTransactionResponse;
import com.shreespark.pos_api.payment.entity.PaymentTransaction;
import com.shreespark.pos_api.payment.mapper.PaymentMapper;
import com.shreespark.pos_api.payment.repository.PaymentTransactionRepository;
import com.shreespark.pos_api.payment.service.PaymentService;
import com.shreespark.pos_api.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentTransaction record(UUID tenantId, UUID saleId, UUID customerId,
                                     PaymentMethod method, BigDecimal amount, String referenceNumber) {
        PaymentTransaction tx = PaymentTransaction.builder()
                .saleId(saleId)
                .customerId(customerId)
                .method(method)
                .status(PaymentStatus.SUCCESS)
                .amount(amount)
                .referenceNumber(referenceNumber)
                .build();
        tx.setTenantId(tenantId);
        return paymentRepository.save(tx);
    }

    @Override
    @Transactional
    public void handleUpiWebhook(UUID tenantId, UpiWebhookRequest request) {
        saleRepository.findByInvoiceNumberAndTenantId(request.invoiceNumber(), tenantId)
                .ifPresent(sale -> {
                    PaymentStatus status = "SUCCESS".equalsIgnoreCase(request.status())
                            ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

                    PaymentTransaction tx = PaymentTransaction.builder()
                            .saleId(sale.getId())
                            .customerId(sale.getCustomerId())
                            .method(PaymentMethod.UPI)
                            .status(status)
                            .amount(request.amount())
                            .referenceNumber(request.referenceNumber())
                            .note("UPI webhook: " + request.transactionId())
                            .build();
                    tx.setTenantId(tenantId);
                    paymentRepository.save(tx);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getBySale(UUID tenantId, UUID saleId) {
        return paymentRepository.findAllBySaleIdAndTenantId(saleId, tenantId)
                .stream().map(paymentMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getByCustomer(UUID tenantId, UUID customerId) {
        return paymentRepository.findAllByCustomerIdAndTenantId(customerId, tenantId)
                .stream().map(paymentMapper::toResponse).toList();
    }
}
