package com.shreespark.pos_api.membership.repository;

import com.shreespark.pos_api.common.enums.MembershipTier;
import com.shreespark.pos_api.membership.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findAllByTenantIdAndActiveTrue(UUID tenantId);
    Optional<Membership> findByIdAndTenantIdAndActiveTrue(UUID id, UUID tenantId);
    Optional<Membership> findByTierAndTenantIdAndActiveTrue(MembershipTier tier, UUID tenantId);
    boolean existsByTierAndTenantId(MembershipTier tier, UUID tenantId);
}
