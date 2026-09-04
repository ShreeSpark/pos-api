package com.shreespark.pos_api.sync.dto.request;

import com.shreespark.pos_api.common.enums.SyncOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SyncBatchRequest(
        @NotNull List<SyncItem> items
) {
    public record SyncItem(
            @NotBlank String clientRequestId,
            @NotNull SyncOperation operation,
            @NotNull Long sequence,
            @NotBlank String payload
    ) {}
}
