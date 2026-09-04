package com.shreespark.pos_api.barcode.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.BarcodeFormat;
import com.shreespark.pos_api.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "barcodes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "value"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barcode extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BarcodeFormat format;

    @Column(columnDefinition = "TEXT")
    private String imageBase64;
}
