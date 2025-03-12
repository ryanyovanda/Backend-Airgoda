package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyUsecase propertyUsecase;
    private final UsersRepository usersRepository;

    @Autowired
    public PropertyController(PropertyUsecase propertyUsecase, UsersRepository userRepository) {
        this.propertyUsecase = propertyUsecase;
        this.usersRepository = userRepository;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PropertyResponseDTO> createProperty(
            @RequestPart(value = "data") String requestData,
            @RequestPart(value = "images") List<MultipartFile> images) {

        // Get authenticated user from JWT token in SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof org.springframework.security.oauth2.jwt.Jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) principal;
        String email = jwt.getClaim("sub"); // Extract email or username from JWT claims

        // Fetch the authenticated user from the database
        User authenticatedUser = usersRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate images
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

        // Set tenant ID automatically
        requestDTO.setTenantId(authenticatedUser.getId());

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

    @GetMapping("/tenant")
    public ResponseEntity<List<PropertyResponseDTO>> getTenantProperties() {
        // Get authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();

        // Find authenticated user
        User authenticatedUser = usersRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PropertyResponseDTO> properties = propertyUsecase.getPropertiesByTenant(authenticatedUser.getId());
        return ResponseEntity.ok(properties);
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
