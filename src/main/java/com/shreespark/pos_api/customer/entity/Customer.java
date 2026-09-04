package com.shreespark.pos_api.customer.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.CustomerType;
import com.shreespark.pos_api.membership.entity.MembershipSubscription;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String phone;

    private String email;

    private String address;

    private String gstin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerType type = CustomerType.RETAIL;

    // credit limit — 0 means no credit allowed
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.ZERO;

    // outstanding balance — increases on credit sale, decreases on payment
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MembershipSubscription> membershipSubscriptions = new ArrayList<>();
}
