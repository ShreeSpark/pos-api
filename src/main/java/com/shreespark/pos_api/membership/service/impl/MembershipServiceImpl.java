package com.shreespark.pos_api.membership.service.impl;

import com.shreespark.pos_api.common.enums.MembershipStatus;
import com.shreespark.pos_api.common.exception.ResourceNotFoundException;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.customer.repository.CustomerRepository;
import com.shreespark.pos_api.membership.dto.request.AssignMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.CreateMembershipRequest;
import com.shreespark.pos_api.membership.dto.request.UpdateMembershipRequest;
import com.shreespark.pos_api.membership.dto.response.MembershipResponse;
import com.shreespark.pos_api.membership.dto.response.MembershipSubscriptionResponse;
import com.shreespark.pos_api.membership.entity.Membership;
import com.shreespark.pos_api.membership.entity.MembershipSubscription;
import com.shreespark.pos_api.membership.mapper.MembershipMapper;
import com.shreespark.pos_api.membership.repository.MembershipRepository;
import com.shreespark.pos_api.membership.repository.MembershipSubscriptionRepository;
import com.shreespark.pos_api.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipSubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final MembershipMapper membershipMapper;

    @Override
    @Transactional
    public MembershipResponse create(UUID tenantId, CreateMembershipRequest request) {
        if (membershipRepository.existsByTierAndTenantId(request.tier(), tenantId)) {
            throw new RuntimeException("Membership tier already exists: " + request.tier());
        }
        Membership membership = Membership.builder()
                .tier(request.tier())
                .name(request.name())
                .discountPercent(request.discountPercent())
                .minPurchaseAmount(request.minPurchaseAmount())
                .validityDays(request.validityDays())
                .description(request.description())
                .build();
        membership.setTenantId(tenantId);
        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    public MembershipResponse getById(UUID tenantId, UUID id) {
        return membershipMapper.toResponse(findOrThrow(tenantId, id));
    }

    @Override
    public List<MembershipResponse> getAll(UUID tenantId) {
        return membershipRepository.findAllByTenantIdAndActiveTrue(tenantId)
                .stream().map(membershipMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public MembershipResponse update(UUID tenantId, UUID id, UpdateMembershipRequest request) {
        Membership membership = findOrThrow(tenantId, id);
        if (request.name() != null) membership.setName(request.name());
        if (request.discountPercent() != null) membership.setDiscountPercent(request.discountPercent());
        if (request.minPurchaseAmount() != null) membership.setMinPurchaseAmount(request.minPurchaseAmount());
        if (request.validityDays() != null) membership.setValidityDays(request.validityDays());
        if (request.description() != null) membership.setDescription(request.description());
        return membershipMapper.toResponse(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    public void delete(UUID tenantId, UUID id) {
        Membership membership = findOrThrow(tenantId, id);
        membership.setActive(false);
        membership.setDeletedAt(Instant.now());
        membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public MembershipSubscriptionResponse assign(UUID tenantId, UUID customerId, AssignMembershipRequest request) {
        Customer customer = customerRepository.findByIdAndTenantIdAndActiveTrue(customerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        Membership membership = findOrThrow(tenantId, request.membershipId());

        // expire any existing active subscription
        subscriptionRepository.findActiveByCustomerId(customerId, LocalDate.now())
                .ifPresent(existing -> {
                    existing.setStatus(MembershipStatus.CANCELLED);
                    subscriptionRepository.save(existing);
                });

        LocalDate start = LocalDate.now();
        LocalDate end   = start.plusDays(membership.getValidityDays());

        MembershipSubscription subscription = MembershipSubscription.builder()
                .customer(customer)
                .membership(membership)
                .startDate(start)
                .endDate(end)
                .status(MembershipStatus.ACTIVE)
                .build();
        subscription.setTenantId(tenantId);

        return membershipMapper.toSubscriptionResponse(subscriptionRepository.save(subscription));
    }

    @Override
    public Optional<MembershipSubscriptionResponse> getActiveSubscription(UUID tenantId, UUID customerId) {
        return subscriptionRepository.findActiveByCustomerId(customerId, LocalDate.now())
                .map(membershipMapper::toSubscriptionResponse);
    }

    @Override
    public List<MembershipSubscriptionResponse> getCustomerSubscriptions(UUID tenantId, UUID customerId) {
        return subscriptionRepository.findAllByCustomerIdAndCustomerTenantId(customerId, tenantId)
                .stream().map(membershipMapper::toSubscriptionResponse).toList();
    }

    private Membership findOrThrow(UUID tenantId, UUID id) {
        return membershipRepository.findByIdAndTenantIdAndActiveTrue(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", id));
    }
}
