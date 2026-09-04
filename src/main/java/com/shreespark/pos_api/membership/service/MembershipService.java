package com.shreespark.pos_api.membership.service;

import com.shreespark.pos_api.membership.dto.request.AssignMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.CreateMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.UpdateMembershipRequest;
import com.shreespark.pos_api.membership.dto.response.MembershipResponse;
import com.shreespark.pos_api.membership.dto.response.MembershipSubscriptionResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipService {
    MembershipResponse create(UUID tenantId, CreateMembershipRequest request);
    MembershipResponse getById(UUID tenantId, UUID id);
    List<MembershipResponse> getAll(UUID tenantId);
    MembershipResponse update(UUID tenantId, UUID id, UpdateMembershipRequest request);
    void delete(UUID tenantId, UUID id);

    // assign membership to customer
    MembershipSubscriptionResponse assign(UUID tenantId, UUID customerId, AssignMembershipRequest request);

    // get active subscription for billing discount resolution
    Optional<MembershipSubscriptionResponse> getActiveSubscription(UUID tenantId, UUID customerId);

    List<MembershipSubscriptionResponse> getCustomerSubscriptions(UUID tenantId, UUID customerId);
}
