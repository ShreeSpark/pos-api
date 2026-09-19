package com.shreespark.pos_api.lead.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String businessName;
    private String city;
    private String planName; // e.g. Starter, Retail Pro, Enterprise

    @Column(nullable = false)
    private String queryType; // LEAD, CONTACT, CUSTOM_DEMO

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    @Builder.Default
    private String status = "NEW"; // NEW, IN_PROGRESS, CONTACTED, CLOSED

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "NEW";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
