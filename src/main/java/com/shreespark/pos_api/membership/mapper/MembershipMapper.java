package com.shreespark.pos_api.membership.mapper;

import com.shreespark.pos_api.membership.dto.response.MembershipResponse;
import com.shreespark.pos_api.membership.dto.response.MembershipSubscriptionResponse;
import com.shreespark.pos_api.membership.entity.Membership;
import com.shreespark.pos_api.membership.entity.MembershipSubscription;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {

    public MembershipResponse toResponse(Membership m) {
        return new MembershipResponse(
                m.getId(),
                m.getTier().name(),
                m.getName(),
                m.getDiscountPercent(),
                m.getMinPurchaseAmount(),
                m.getValidityDays(),
                m.getDescription(),
                m.getCreatedAt()
        );
    }

    public MembershipSubscriptionResponse toSubscriptionResponse(MembershipSubscription ms) {
        return new MembershipSubscriptionResponse(
                ms.getId(),
                ms.getCustomer().getId(),
                ms.getCustomer().getName(),
                ms.getMembership().getTier().name(),
                ms.getMembership().getName(),
                ms.getMembership().getDiscountPercent(),
                ms.getStartDate(),
                ms.getEndDate(),
                ms.getStatus().name()
        );
    }
}
