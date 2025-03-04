package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePropertyRequestDTO {
    private String name;
    private String description;
    private String roomId;
    private Long categoryId;
}