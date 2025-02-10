package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collections;

@Repository
public interface RoomVariantRepository extends JpaRepository<RoomVariant, Long> {

    @Query("SELECT rv FROM RoomVariant rv WHERE rv.property.id = :propertyId")
    List<RoomVariant> findByPropertyId(@Param("propertyId") Long propertyId);

    default List<RoomVariant> safeFindByPropertyId(Long propertyId) {
        List<RoomVariant> result = findByPropertyId(propertyId);
        return result != null ? result : Collections.emptyList();
    }
}
