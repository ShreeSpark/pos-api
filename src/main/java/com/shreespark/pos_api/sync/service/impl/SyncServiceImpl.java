package com.shreespark.pos_api.sync.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shreespark.pos_api.common.enums.SyncOperation;
import com.shreespark.pos_api.common.enums.SyncStatus;
import com.shreespark.pos_api.customer.dto.request.CreateCustomerRequest;
import com.shreespark.pos_api.customer.dto.request.UpdateCustomerRequest;
import com.shreespark.pos_api.customer.service.CustomerService;
import com.shreespark.pos_api.inventory.dto.request.StockAdjustmentRequest;
import com.shreespark.pos_api.inventory.service.InventoryService;
import com.shreespark.pos_api.sales.dto.request.CreateSaleRequest;
import com.shreespark.pos_api.sales.service.SaleService;
import com.shreespark.pos_api.sync.dto.request.SyncBatchRequest;
import com.shreespark.pos_api.sync.dto.response.SyncBatchResponse;
import com.shreespark.pos_api.sync.entity.SyncQueue;
import com.shreespark.pos_api.sync.repository.SyncQueueRepository;
import com.shreespark.pos_api.sync.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncServiceImpl implements SyncService {

    private final SyncQueueRepository syncQueueRepository;
    private final SaleService saleService;
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SyncBatchResponse processBatch(UUID tenantId, UUID deviceId, SyncBatchRequest request) {
        List<SyncBatchResponse.SyncItemResult> results = new ArrayList<>();

        for (SyncBatchRequest.SyncItem item : request.items()) {
            // idempotency check
            var existing = syncQueueRepository.findByClientRequestIdAndTenantId(
                    item.clientRequestId(), tenantId);
            if (existing.isPresent()) {
                SyncQueue q = existing.get();
                results.add(toResult(q));
                continue;
            }

            SyncQueue queue = SyncQueue.builder()
                    .deviceId(deviceId)
                    .clientRequestId(item.clientRequestId())
                    .operation(item.operation())
                    .payload(item.payload())
                    .sequence(item.sequence())
                    .status(SyncStatus.PENDING)
                    .build();
            queue.setTenantId(tenantId);

            try {
                String resultId = dispatch(tenantId, deviceId, item);
                queue.setStatus(SyncStatus.SYNCED);
                queue.setResultId(resultId);
            } catch (Exception e) {
                log.warn("Sync failed for clientRequestId={}: {}", item.clientRequestId(), e.getMessage());
                queue.setStatus(SyncStatus.FAILED);
                queue.setErrorMessage(e.getMessage());
            }

            syncQueueRepository.save(queue);
            results.add(toResult(queue));
        }

        int synced = (int) results.stream().filter(r -> r.status() == SyncStatus.SYNCED).count();
        int failed = (int) results.stream().filter(r -> r.status() == SyncStatus.FAILED).count();
        return new SyncBatchResponse(results.size(), synced, failed, results);
    }

    @Override
    @Transactional(readOnly = true)
    public SyncBatchResponse getDeviceHistory(UUID tenantId, UUID deviceId) {
        List<SyncQueue> queues = syncQueueRepository
                .findAllByDeviceIdAndTenantIdOrderBySequenceAsc(deviceId, tenantId);
        List<SyncBatchResponse.SyncItemResult> results = queues.stream().map(this::toResult).toList();
        int synced = (int) results.stream().filter(r -> r.status() == SyncStatus.SYNCED).count();
        int failed = (int) results.stream().filter(r -> r.status() == SyncStatus.FAILED).count();
        return new SyncBatchResponse(results.size(), synced, failed, results);
    }

    private String dispatch(UUID tenantId, UUID deviceId, SyncBatchRequest.SyncItem item) throws Exception {
        return switch (item.operation()) {
            case CREATE_SALE -> {
                CreateSaleRequest req = objectMapper.readValue(item.payload(), CreateSaleRequest.class);
                yield saleService.create(tenantId, deviceId, req).id().toString();
            }
            case STOCK_ADJUSTMENT -> {
                StockAdjustmentRequest req = objectMapper.readValue(item.payload(), StockAdjustmentRequest.class);
                yield inventoryService.adjust(tenantId, "sync:" + deviceId, req).id().toString();
            }
            case CREATE_CUSTOMER -> {
                CreateCustomerRequest req = objectMapper.readValue(item.payload(), CreateCustomerRequest.class);
                yield customerService.create(tenantId, req).id().toString();
            }
            case UPDATE_CUSTOMER -> {
                // payload: {"id":"<uuid>", ...UpdateCustomerRequest fields}
                var node = objectMapper.readTree(item.payload());
                UUID customerId = UUID.fromString(node.get("id").asText());
                UpdateCustomerRequest req = objectMapper.treeToValue(node, UpdateCustomerRequest.class);
                yield customerService.update(tenantId, customerId, req).id().toString();
            }
        };
    }

    private SyncBatchResponse.SyncItemResult toResult(SyncQueue q) {
        return new SyncBatchResponse.SyncItemResult(
                q.getId(), q.getClientRequestId(), q.getOperation(),
                q.getStatus(), q.getResultId(), q.getErrorMessage());
    }
}
