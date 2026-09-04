package com.shreespark.pos_api.customer.mapper;

import com.shreespark.pos_api.customer.dto.response.CustomerResponse;
import com.shreespark.pos_api.customer.entity.Customer;
import com.shreespark.pos_api.membership.entity.MembershipSubscription;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer c, Optional<MembershipSubscription> activeSub) {
        return new CustomerResponse(
                c.getId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress(),
                c.getGstin(),
                c.getType().name(),
                c.getCreditLimit(),
                c.getOutstandingBalance(),
                c.getOutstandingBalance().compareTo(c.getCreditLimit()) >= 0,
                activeSub.map(ms -> new CustomerResponse.ActiveMembership(
                        ms.getId(),
                        ms.getMembership().getId(),
                        ms.getMembership().getTier().name(),
                        ms.getMembership().getName(),
                        ms.getMembership().getDiscountPercent(),
                        ms.getStartDate(),
                        ms.getEndDate(),
                        ms.getStatus().name()
                )).orElse(null),
                c.getCreatedAt()
        );
    }
}
