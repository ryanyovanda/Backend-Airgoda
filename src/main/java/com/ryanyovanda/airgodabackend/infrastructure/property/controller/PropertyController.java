package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyUsecase propertyUsecase;

    @Autowired
    public PropertyController(PropertyUsecase propertyUsecase) {
        this.propertyUsecase = propertyUsecase;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PropertyResponseDTO> createProperty(
            @RequestPart(value = "data") String requestData,
            @RequestPart(value = "images") List<MultipartFile> images) {

        for (MultipartFile image : images) {
            if (image.getSize() > 1024 * 1024) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        CreatePropertyRequestDTO requestDTO;

        try {
            requestDTO = objectMapper.readValue(requestData, CreatePropertyRequestDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(null);
        }

        PropertyResponseDTO savedProperty = propertyUsecase.createProperty(requestDTO, images);
        return ResponseEntity.ok(savedProperty);
    }

    @GetMapping
    public ResponseEntity<Page<PropertyResponseDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponseDTO> properties = propertyUsecase.getProperties(pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(@PathVariable Long id) {
        Optional<PropertyResponseDTO> property = propertyUsecase.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> updateProperty(
            @PathVariable Long id,
            @RequestBody CreatePropertyRequestDTO requestDTO) {

        PropertyResponseDTO updatedProperty = propertyUsecase.updateProperty(id, requestDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyUsecase.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sorted/cheapest")
    public ResponseEntity<Page<PropertyResponseDTO>> getPropertiesSortedByCheapestPrice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponseDTO> properties = propertyUsecase.getPropertiesSortedByCheapestPrice(pageable);
        return ResponseEntity.ok(properties);
    }

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

    @DeleteMapping("/image/{imageId}")
    public ResponseEntity<Void> deletePropertyImage(@PathVariable Long imageId) {
        propertyUsecase.deletePropertyImage(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/images")
    public ResponseEntity<PropertyResponseDTO> updatePropertyImages(
            @PathVariable Long id,
            @RequestPart List<MultipartFile> images) {

        for (MultipartFile image : images) {
            if (image.getSize() > 1024 * 1024) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        PropertyResponseDTO updatedProperty = propertyUsecase.updatePropertyImages(id, images);
        return ResponseEntity.ok(updatedProperty);
    }
}
