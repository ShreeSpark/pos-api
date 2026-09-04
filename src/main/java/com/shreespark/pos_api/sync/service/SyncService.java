package com.shreespark.pos_api.sync.service;

import com.shreespark.pos_api.sync.dto.request.SyncBatchRequest;
import com.shreespark.pos_api.sync.dto.response.SyncBatchResponse;

import java.util.UUID;

public interface SyncService {
    SyncBatchResponse processBatch(UUID tenantId, UUID deviceId, SyncBatchRequest request);
    SyncBatchResponse getDeviceHistory(UUID tenantId, UUID deviceId);
}
