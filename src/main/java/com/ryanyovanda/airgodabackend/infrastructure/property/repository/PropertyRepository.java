package com.ryanyovanda.airgodabackend.infrastructure.property.repository;

import com.ryanyovanda.airgodabackend.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {


    List<Property> findByIsActiveTrue();


    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.category WHERE p.tenant.id = :tenantId")
    List<Property> findByOwnerId(@Param("tenantId") Long tenantId);


    List<Property> findByLocationId(Long locationId);
    @Override
    Optional<Property> findById(Long propertyId);

    // New methods with pagination
    Page<Property> findByIsActiveTrue(Pageable pageable);
    Page<Property> findByLocationIdAndIsActiveTrue(Long locationId, Pageable pageable);
    Page<Property> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    Page<Property> findByLocationIdAndCategoryIdAndIsActiveTrue(Long locationId, Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE (:locationId IS NULL OR p.location.id = :locationId) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))" )
    Page<Property> searchProperties(
            @Param("locationId") Long locationId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findAllSortedByCheapestPrice(Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.location.id = :locationId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByLocationSortedByCheapestPrice(@Param("locationId") Long locationId, Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.category.id = :categoryId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByCategorySortedByCheapestPrice(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.location.id = :locationId AND p.category.id = :categoryId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByLocationAndCategorySortedByCheapestPrice(@Param("locationId") Long locationId, @Param("categoryId") Long categoryId, Pageable pageable);
}
