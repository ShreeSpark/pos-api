package com.shreespark.pos_api.khata.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.KhataEntryType;
import com.shreespark.pos_api.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "khata_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhataEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KhataEntryType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    // reference to sale or payment
    private UUID referenceId;

    private String note;
}
