package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PeakRateResponseDTO {
    private Long id;
    private Long roomVariantId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal additionalPrice;
}
