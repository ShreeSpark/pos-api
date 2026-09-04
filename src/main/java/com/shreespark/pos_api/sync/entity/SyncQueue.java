package com.shreespark.pos_api.sync.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.SyncOperation;
import com.shreespark.pos_api.common.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "sync_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncQueue extends BaseEntity {

    @Column(nullable = false)
    private UUID deviceId;

    // client-generated idempotency key to prevent duplicate processing
    @Column(nullable = false, unique = true)
    private String clientRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncOperation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SyncStatus status = SyncStatus.PENDING;

    // JSON payload of the operation
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    // server-assigned result ID after processing (e.g. saleId, customerId)
    private String resultId;

    private String errorMessage;

    // sequence number from device for ordering
    @Column(nullable = false)
    private Long sequence;
}
