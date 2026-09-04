package com.shreespark.pos_api.membership.repository;

import com.shreespark.pos_api.common.enums.MembershipStatus;
import com.shreespark.pos_api.membership.entity.MembershipSubscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipSubscriptionRepository extends JpaRepository<MembershipSubscription, UUID> {

    // get active subscription for a customer
    @Query("SELECT ms FROM MembershipSubscription ms WHERE ms.customer.id = :customerId " +
           "AND ms.status = 'ACTIVE' AND ms.endDate >= :today")
    Optional<MembershipSubscription> findActiveByCustomerId(
            @Param("customerId") UUID customerId,
            @Param("today") LocalDate today);

    List<MembershipSubscription> findAllByCustomerIdAndCustomerTenantId(UUID customerId, UUID tenantId);

    // find all subscriptions expiring soon (for reminders)
    @Query("SELECT ms FROM MembershipSubscription ms WHERE ms.customer.tenantId = :tenantId " +
           "AND ms.status = 'ACTIVE' AND ms.endDate BETWEEN :today AND :expiryDate")
    List<MembershipSubscription> findExpiringSoon(
            @Param("tenantId") UUID tenantId,
            @Param("today") LocalDate today,
            @Param("expiryDate") LocalDate expiryDate);
}
