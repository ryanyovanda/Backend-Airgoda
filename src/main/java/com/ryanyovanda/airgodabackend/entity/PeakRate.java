package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "peak_rate")
public class PeakRate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "peak_rate_id_gen")
    @SequenceGenerator(name = "peak_rate_id_gen", sequenceName = "peak_rate_peak_rate_id_seq", allocationSize = 1)
    @Column(name = "peak_rate_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_variant_id", nullable = false)
    private RoomVariant roomVariant;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "additional_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal additionalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // ✅ Set otomatis timestamps sebelum insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    // ✅ Update timestamps sebelum update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
