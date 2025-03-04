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
    private String categoryName; // New field for category name
}
