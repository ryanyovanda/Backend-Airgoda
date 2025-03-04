package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.PropertyCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long> {

    Optional<PropertyCategory> findByNameIgnoreCase(String name);

    List<PropertyCategory> findAllByIsDeletedFalse();
}
