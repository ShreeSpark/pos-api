package com.shreespark.pos_api.subscription.entity;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscription_plan_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionPlanConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private SubscriptionPlan plan;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal yearlyPrice;

    @Column(nullable = false)
    private int maxDevices;

    @Column(nullable = false)
    private int maxStaff;

    // -1 = unlimited
    @Column(nullable = false)
    private int maxProducts;

    private String features;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
