package com.shreespark.pos_api.device.entity;

import com.shreespark.pos_api.common.BaseEntity;
import com.shreespark.pos_api.common.enums.DeviceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String deviceCode;

    @Column(nullable = false)
    private String deviceName;

    // e.g. "Android", "Windows", "iOS"
    private String platform;

    private String appVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeviceStatus status = DeviceStatus.ACTIVE;

    private Instant lastSeenAt;

    private String registeredBy;
}
