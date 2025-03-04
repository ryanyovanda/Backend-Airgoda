package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyCategoryRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyCategoryResponseDTO;

import java.util.List;

public interface PropertyCategoryUsecase {

    PropertyCategoryResponseDTO createCategory(CreatePropertyCategoryRequestDTO requestDTO);

    List<PropertyCategoryResponseDTO> getAllCategories();

    PropertyCategoryResponseDTO getCategoryById(Long id);

    PropertyCategoryResponseDTO updateCategory(Long id, CreatePropertyCategoryRequestDTO requestDTO);

    void softDeleteCategory(Long id);
}
