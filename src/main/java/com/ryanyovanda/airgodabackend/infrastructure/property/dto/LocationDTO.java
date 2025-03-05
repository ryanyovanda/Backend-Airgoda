package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDTO {
    private Long id;
    private String name;
    private String type; // CITY, REGENCY, etc.

    public LocationDTO(Long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
