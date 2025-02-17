package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ Auto-generate ID
    @Column(name = "discount_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_variant_id", nullable = false)
    private RoomVariant roomVariant;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @Column(name = "discount_type", nullable = false)
    private String discountType; // "percentage" atau "fixed"

    @NotNull
    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;
}
