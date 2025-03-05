package com.ryanyovanda.airgodabackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Location parent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    public enum LocationType {
        COUNTRY, ISLAND, PROVINCE, REGENCY, CITY
    }
}
