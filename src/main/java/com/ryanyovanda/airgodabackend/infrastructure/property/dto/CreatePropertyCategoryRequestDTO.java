package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePropertyCategoryRequestDTO {

    @NotNull
    @Size(max = 50)
    private String name;
}
