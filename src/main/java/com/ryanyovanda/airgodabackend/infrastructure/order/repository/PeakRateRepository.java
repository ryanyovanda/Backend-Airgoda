package com.ryanyovanda.airgodabackend.infrastructure.order.repository;

import com.ryanyovanda.airgodabackend.entity.PeakRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

@Repository
public interface PeakRateRepository extends JpaRepository<PeakRate, Long> {

    @Query("SELECT p FROM PeakRate p WHERE p.roomVariant.id = :roomVariantId " +
            "AND (:startDate BETWEEN p.startDate AND p.endDate OR :endDate BETWEEN p.startDate AND p.endDate) " +
            "ORDER BY p.startDate DESC LIMIT 1")
    PeakRate findValidPeakRate(@Param("roomVariantId") Long roomVariantId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
