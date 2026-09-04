package com.shreespark.pos_api.category.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.gst.entity.GstRate;
import com.shreespark.pos_api.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    // HSN code for all products in this category
    private String hsnCode;

    // GST slab — ManyToOne to gst_rates table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gst_rate_id")
    private GstRate gstRate;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
