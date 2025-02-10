package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class GetRoomVariantDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer capacity;
    private String facilities;
    private Long propertyId;
}
