package com.shreespark.pos_api.product.entity;

import com.shreespark.pos_api.barcode.entity.Barcode;
import com.shreespark.pos_api.brand.entity.Brand;
import com.shreespark.pos_api.category.entity.Category;
import com.shreespark.pos_api.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true)
    private String sku;

    private String imageUrl;

    // Pricing tiers
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal retailPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal wholesalePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal dealerPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal costPrice;

    // Stock config — thresholds only, actual stock lives in inventory
    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Column(nullable = false)
    @Builder.Default
    private Integer moq = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Barcode> barcodes = new ArrayList<>();
}
