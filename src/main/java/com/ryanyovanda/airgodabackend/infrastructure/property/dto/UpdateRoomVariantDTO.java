package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateRoomVariantDTO {
    @NotNull(message = "Room Variant ID is required")
    private Long id;

    @Size(min = 3, max = 100, message = "Room name must be between 3 and 100 characters")
    private String name;

    private BigDecimal price;

    private Integer maxGuest; // Added to allow updating the number of guests per room

    private Integer capacity; // Represents how many rooms are available

    private List<String> facilities;
}
