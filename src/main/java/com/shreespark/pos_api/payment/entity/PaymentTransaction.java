package com.shreespark.pos_api.payment.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.PaymentMethod;
import com.shreespark.pos_api.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction extends BaseEntity {

    @Column(nullable = false)
    private UUID saleId;

    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.SUCCESS;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // UPI reference / card last 4 / transaction id
    private String referenceNumber;

    private String note;
}
