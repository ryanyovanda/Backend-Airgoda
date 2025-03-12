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

    // Existing methods
    List<Property> findByIsActiveTrue();

//    @Query("SELECT p FROM Property p WHERE p.tenant.id = :tenantId")
//    List<Property> findByOwnerId(@Param("tenantId") Long tenantId);

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


    // Sorting by cheapest available room price
    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findAllSortedByCheapestPrice(Pageable pageable);

    // Filtering by location and sorting by cheapest price
    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.location.id = :locationId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByLocationSortedByCheapestPrice(@Param("locationId") Long locationId, Pageable pageable);

    // Filtering by category and sorting by cheapest price
    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.category.id = :categoryId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByCategorySortedByCheapestPrice(@Param("categoryId") Long categoryId, Pageable pageable);

    // Filtering by location & category, sorted by cheapest price
    @Query("""
        SELECT p FROM Property p
        JOIN RoomVariant rv ON rv.property.id = p.id
        WHERE p.isActive = true AND p.location.id = :locationId AND p.category.id = :categoryId
        GROUP BY p.id
        ORDER BY MIN(rv.price) ASC
    """)
    Page<Property> findByLocationAndCategorySortedByCheapestPrice(@Param("locationId") Long locationId, @Param("categoryId") Long categoryId, Pageable pageable);
}
