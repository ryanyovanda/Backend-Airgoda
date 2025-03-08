package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "properties_id_gen")
    @SequenceGenerator(name = "properties_id_gen", sequenceName = "properties_property_id_seq", allocationSize = 1)
    @Column(name = "property_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 100)
    @Column(name = "room_id", length = 100)
    private String roomId;  // Ensure this is not a foreign key reference

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private User tenant;  // Ensure `User` entity exists

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PropertyCategory category;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>(); // ✅ Initialize list to avoid null errors

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // **Fix: Use @PrePersist to set default values**
    @PrePersist
    public void prePersist() {
        if (isActive == null) isActive = true;
        if (isDeleted == null) isDeleted = false;
        if (createdAt == null) createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    // **Fix: Use @PreUpdate to update timestamps**
    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
