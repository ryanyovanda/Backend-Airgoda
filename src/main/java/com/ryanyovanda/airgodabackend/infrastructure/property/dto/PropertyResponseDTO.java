package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PropertyResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String roomId;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private Long categoryId; // New field for category name
    private LocationDTO location;
}
