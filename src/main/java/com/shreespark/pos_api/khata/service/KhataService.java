package com.shreespark.pos_api.khata.service;

import com.shreespark.pos_api.common.enums.KhataEntryType;
import com.shreespark.pos_api.khata.dto.request.ManualKhataEntryRequest;
import com.shreespark.pos_api.khata.dto.request.RecordPaymentRequest;
import com.shreespark.pos_api.khata.dto.response.KhataEntryResponse;
import com.shreespark.pos_api.khata.entity.KhataEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface KhataService {

    // called internally by sales — returns entity so sales can reference it
    KhataEntry recordEntry(UUID tenantId, UUID customerId, KhataEntryType type,
                           BigDecimal amount, UUID referenceId, String note);

    // manual entry by staff
    KhataEntryResponse manualEntry(UUID tenantId, UUID customerId, ManualKhataEntryRequest request);

    // customer pays outstanding balance
    KhataEntryResponse recordPayment(UUID tenantId, UUID customerId, RecordPaymentRequest request);

    List<KhataEntryResponse> getByCustomer(UUID tenantId, UUID customerId);
}
