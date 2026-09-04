package com.shreespark.pos_api.inventory.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_adjustments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer adjustedQuantity;

    @Column(nullable = false)
    private Integer stockBefore;

    @Column(nullable = false)
    private Integer stockAfter;

    @Column(nullable = false)
    private String reason;

    private String approvedBy;
}
