package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreatePropertyRequestDTO {
    private String name;
    private String description;
    private String roomId;
    private Long categoryId;
    private Long locationId;
    private List<String> imageUrls;
    private List<String> imageId;
    private String fullAddress;
    private Long tenantId;
    private Boolean isActive;


}