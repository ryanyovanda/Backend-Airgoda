package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "property_categories")
public class PropertyCategory {

    @Id
    @SequenceGenerator(
            name = "property_category_id_gen",
            sequenceName = "property_categories_id_gen",
            allocationSize = 1 // Make sure this matches the database sequence increment size
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "property_category_id_gen")
    @Column(name = "category_id", nullable = false)
    private Long id;


    @NotNull
    @Size(max = 50)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> properties;
}
