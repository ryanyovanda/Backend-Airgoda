package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyUsecase propertyUsecase;

    @Autowired
    public PropertyController(PropertyUsecase propertyUsecase) {
        this.propertyUsecase = propertyUsecase;
    }

    // ✅ Create a new property
    @PostMapping
    public ResponseEntity<PropertyResponseDTO> createProperty(@RequestBody CreatePropertyRequestDTO requestDTO) {
        PropertyResponseDTO savedProperty = propertyUsecase.createProperty(requestDTO);
        return ResponseEntity.ok(savedProperty);
    }

    // ✅ Get paginated properties
    @GetMapping
    public ResponseEntity<Page<PropertyResponseDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponseDTO> properties = propertyUsecase.getProperties(pageable);
        return ResponseEntity.ok(properties);
    }

    // ✅ Get a property by ID
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(@PathVariable Long id) {
        Optional<PropertyResponseDTO> property = propertyUsecase.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Update property
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> updateProperty(@PathVariable Long id, @RequestBody CreatePropertyRequestDTO requestDTO) {
        PropertyResponseDTO updatedProperty = propertyUsecase.updateProperty(id, requestDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    // ✅ Delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyUsecase.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Get properties sorted by cheapest price
    @GetMapping("/sorted/cheapest")
    public ResponseEntity<Page<PropertyResponseDTO>> getPropertiesSortedByCheapestPrice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponseDTO> properties = propertyUsecase.getPropertiesSortedByCheapestPrice(pageable);
        return ResponseEntity.ok(properties);
    }

    // ✅ Get properties filtered by location and/or category
    @GetMapping("/filter")
    public ResponseEntity<Page<PropertyResponseDTO>> getPropertiesByFilters(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponseDTO> properties;

        if (locationId != null && categoryId != null) {
            properties = propertyUsecase.getPropertiesByLocationAndCategory(locationId, categoryId, pageable);
        } else if (locationId != null) {
            properties = propertyUsecase.getPropertiesByLocation(locationId, pageable);
        } else if (categoryId != null) {
            properties = propertyUsecase.getPropertiesByCategory(categoryId, pageable);
        } else {
            properties = propertyUsecase.getProperties(pageable);
        }

        return ResponseEntity.ok(properties);
    }
}
