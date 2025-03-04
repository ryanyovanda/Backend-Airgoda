package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<PropertyResponseDTO> createProperty(@RequestBody CreatePropertyRequestDTO requestDTO) {
        PropertyResponseDTO savedProperty = propertyUsecase.createProperty(requestDTO);
        return ResponseEntity.ok(savedProperty);
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponseDTO>> getAllProperties() {
        List<PropertyResponseDTO> properties = propertyUsecase.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(@PathVariable Long id) {
        Optional<PropertyResponseDTO> property = propertyUsecase.getPropertyById(id);
        return property.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDTO> updateProperty(@PathVariable Long id, @RequestBody CreatePropertyRequestDTO requestDTO) {
        PropertyResponseDTO updatedProperty = propertyUsecase.updateProperty(id, requestDTO);
        return ResponseEntity.ok(updatedProperty);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyUsecase.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}
