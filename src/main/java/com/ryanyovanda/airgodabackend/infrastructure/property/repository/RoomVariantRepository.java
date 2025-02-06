package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomVariantRepository extends JpaRepository<RoomVariant, Long> {
    List<RoomVariant> findByPropertyId(Long propertyId);
}
