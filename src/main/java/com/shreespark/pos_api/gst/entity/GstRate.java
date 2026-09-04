package com.shreespark.pos_api.gst.entity;

import com.shreespark.pos_api.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "gst_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstRate extends BaseEntity {

    // e.g. "GST 18%", "GST 5% - Food"
    @Column(nullable = false)
    private String name;

    // e.g. 0, 5, 12, 18, 28
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rate;

    // CGST = rate / 2  (intra-state)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal cgstRate;

    // SGST = rate / 2  (intra-state)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal sgstRate;

    // IGST = rate      (inter-state)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal igstRate;

    // HSN codes that typically fall under this slab (informational)
    private String description;
}
