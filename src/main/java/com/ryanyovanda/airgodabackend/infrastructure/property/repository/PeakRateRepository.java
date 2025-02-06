//package com.ryanyovanda.airgodabackend.infrastructure.property.repository;
//
//import com.ryanyovanda.airgodabackend.entity.PeakRate;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface PeakRateRepository extends JpaRepository<PeakRate, Long> {
//    List<PeakRate> findByRoomVariantId(Long roomVariantId);
//    List<PeakRate> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAnd(java.time.LocalDate startDate, java.time.LocalDate endDate);
//
//}
