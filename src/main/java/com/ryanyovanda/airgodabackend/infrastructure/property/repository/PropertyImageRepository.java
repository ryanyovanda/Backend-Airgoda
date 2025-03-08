package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
}
