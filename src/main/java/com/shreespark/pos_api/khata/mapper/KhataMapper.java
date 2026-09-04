package com.shreespark.pos_api.khata.mapper;

import com.shreespark.pos_api.khata.dto.response.KhataEntryResponse;
import com.shreespark.pos_api.khata.entity.KhataEntry;
import org.springframework.stereotype.Component;

@Component
public class KhataMapper {

    public KhataEntryResponse toResponse(KhataEntry e) {
        return new KhataEntryResponse(
                e.getId(),
                e.getCustomer().getId(),
                e.getCustomer().getName(),
                e.getType().name(),
                e.getAmount(),
                e.getBalanceBefore(),
                e.getBalanceAfter(),
                e.getReferenceId(),
                e.getNote(),
                e.getCreatedAt()
        );
    }
}
