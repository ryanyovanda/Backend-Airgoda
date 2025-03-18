package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateRoomVariantDTO {

    @NotNull(message = "Room name is required")
    @Size(min = 3, max = 100, message = "Room name must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Max guest is required")
    private Integer maxGuest;

    @NotNull(message = "Capacity is required")
    private Integer capacity;

    private List<String> facilities;

    @NotNull(message = "Property ID is required")
    private Long propertyId;
}
