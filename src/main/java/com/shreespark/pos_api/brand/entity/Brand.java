package com.shreespark.pos_api.brand.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
