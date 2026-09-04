package com.shreespark.pos_api.membership.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.MembershipTier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "memberships",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "tier"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipTier tier;

    @Column(nullable = false)
    private String name;

    // discount % applied on retail price at billing
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent;

    // minimum purchase amount to qualify
    @Column(precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;

    // validity in days (e.g. 365)
    @Column(nullable = false)
    private Integer validityDays;

    private String description;
}
