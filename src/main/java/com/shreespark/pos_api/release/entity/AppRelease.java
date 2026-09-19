package com.shreespark.pos_api.release.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_releases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRelease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String targetApp; // e.g. "POS_DESKTOP"

    @Column(nullable = false)
    private String version; // e.g. "1.1.0"

    @Column(nullable = false)
    private Integer versionCode; // e.g. 101

    @Column(columnDefinition = "TEXT")
    private String releaseNotes;

    @Column(nullable = false, length = 1000)
    private String downloadUrl;

    @Builder.Default
    private boolean mandatory = false;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
