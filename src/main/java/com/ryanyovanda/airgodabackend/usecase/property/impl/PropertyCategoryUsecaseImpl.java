package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.PropertyCategory;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyCategoryRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyCategoryResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyCategoryRepository;
import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyCategoryUsecase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyCategoryUsecaseImpl implements PropertyCategoryUsecase {

    private final PropertyCategoryRepository propertyCategoryRepository;

    public PropertyCategoryUsecaseImpl(PropertyCategoryRepository propertyCategoryRepository) {
        this.propertyCategoryRepository = propertyCategoryRepository;
    }

    @Override
    @Transactional
    public PropertyCategoryResponseDTO createCategory(CreatePropertyCategoryRequestDTO requestDTO) {
        if (propertyCategoryRepository.findByNameIgnoreCase(requestDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category name already exists.");
        }

        PropertyCategory category = new PropertyCategory();
        category.setName(requestDTO.getName());
        propertyCategoryRepository.save(category);

        return mapToResponseDTO(category);
    }

    @Override
    public List<PropertyCategoryResponseDTO> getAllCategories() {
        return propertyCategoryRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyCategoryResponseDTO getCategoryById(Long id) {
        PropertyCategory category = propertyCategoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
        return mapToResponseDTO(category);
    }

    @Override
    @Transactional
    public PropertyCategoryResponseDTO updateCategory(Long id, CreatePropertyCategoryRequestDTO requestDTO) {
        PropertyCategory category = propertyCategoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        category.setName(requestDTO.getName());
        propertyCategoryRepository.save(category);
        return mapToResponseDTO(category);
    }

    @Override
    @Transactional
    public void softDeleteCategory(Long id) {
        PropertyCategory category = propertyCategoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        category.setIsDeleted(true);
        propertyCategoryRepository.save(category);
    }

    private PropertyCategoryResponseDTO mapToResponseDTO(PropertyCategory category) {
        PropertyCategoryResponseDTO dto = new PropertyCategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}
