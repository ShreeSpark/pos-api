package com.shreespark.pos_api.sales.entity;

import com.shreespark.pos_api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sale_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleAuditLog extends BaseEntity {

    @Column(name = "sale_id", nullable = false)
    private UUID saleId;

    @Column(nullable = false)
    private String action; // CREATED, EDITED, CANCELLED, PRINTED, CASH_MEMO_PRINTED

    private UUID staffId;
    private String staffName;

    @Column(columnDefinition = "TEXT")
    private String details;
}
