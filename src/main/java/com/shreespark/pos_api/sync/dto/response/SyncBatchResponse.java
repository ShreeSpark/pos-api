package com.shreespark.pos_api.sync.dto.response;

import com.shreespark.pos_api.common.enums.SyncOperation;
import com.shreespark.pos_api.common.enums.SyncStatus;

import java.util.List;
import java.util.UUID;

public record SyncBatchResponse(
        int total,
        int synced,
        int failed,
        List<SyncItemResult> results
) {
    public record SyncItemResult(
            UUID id,
            String clientRequestId,
            SyncOperation operation,
            SyncStatus status,
            String resultId,
            String errorMessage
    ) {}
}
