package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByParentId(Long parentId);
}
