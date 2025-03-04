package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PropertyCategoryResponseDTO {
    private Long id;
    private String name;
    private OffsetDateTime createdAt;
}
