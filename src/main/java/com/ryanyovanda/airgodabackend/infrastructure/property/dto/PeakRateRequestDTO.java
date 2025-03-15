package com.ryanyovanda.airgodabackend.infrastructure.property.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeakRateRequestDTO {
    private Long roomVariantId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal additionalPrice;
}
