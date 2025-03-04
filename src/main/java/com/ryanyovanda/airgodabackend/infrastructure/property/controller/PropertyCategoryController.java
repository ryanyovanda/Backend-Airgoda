package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyCategoryRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyCategoryResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyCategoryUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class PropertyCategoryController {

    private final PropertyCategoryUsecase propertyCategoryUsecase;

    public PropertyCategoryController(PropertyCategoryUsecase propertyCategoryUsecase) {
        this.propertyCategoryUsecase = propertyCategoryUsecase;
    }

    @PostMapping
    public ResponseEntity<PropertyCategoryResponseDTO> createCategory(@RequestBody CreatePropertyCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok(propertyCategoryUsecase.createCategory(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<PropertyCategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(propertyCategoryUsecase.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyCategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyCategoryUsecase.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyCategoryResponseDTO> updateCategory(@PathVariable Long id, @RequestBody CreatePropertyCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok(propertyCategoryUsecase.updateCategory(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCategory(@PathVariable Long id) {
        propertyCategoryUsecase.softDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
