package com.ryanyovanda.airgodabackend.infrastructure.property.repository;


import com.ryanyovanda.airgodabackend.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("SELECT d FROM Discount d WHERE d.roomVariant.id = :roomVariantId " +
            "AND (:startDate BETWEEN d.startDate AND d.endDate OR :endDate BETWEEN d.startDate AND d.endDate) " +
            "ORDER BY d.startDate DESC LIMIT 1")
    Discount findValidDiscount(@Param("roomVariantId") Long roomVariantId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
