package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByIsActiveTrue();
    List<Property> findByLocationId(Long locationId);
    @Override
    Optional<Property> findById(Long PropertyId);


}
