package com.shreespark.pos_api.inventory.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_ledger")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLedger extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStock = 0;
}
