package com.shreespark.pos_api.subscription.repository;

import com.shreespark.pos_api.common.enums.SubscriptionPlan;
import com.shreespark.pos_api.subscription.entity.SubscriptionPlanConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionPlanConfigRepository extends JpaRepository<SubscriptionPlanConfig, UUID> {
    Optional<SubscriptionPlanConfig> findByPlan(SubscriptionPlan plan);
    List<SubscriptionPlanConfig> findAllByActiveTrue();
}
