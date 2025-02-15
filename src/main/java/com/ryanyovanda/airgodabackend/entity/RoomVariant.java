package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "room_variant")
public class RoomVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_variant_id_gen")
    @SequenceGenerator(name = "room_variant_id_gen", sequenceName = "room_variant_room_variant_id_seq", allocationSize = 1)
    @Column(name = "room_variant_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "max_guest", nullable = false)
    private Integer maxGuest;

    @NotNull
    @Column(name="capacity", nullable = false)
    private Integer capacity;

    @NotNull
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @ElementCollection
    @CollectionTable(name = "room_variant_facilities", joinColumns = @JoinColumn(name = "room_variant_id"))
    @Column(name = "facility")
    private List<String> facilities;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
