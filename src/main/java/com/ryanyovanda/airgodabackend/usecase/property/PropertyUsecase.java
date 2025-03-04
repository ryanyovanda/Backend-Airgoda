package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PropertyUsecase {
    PropertyResponseDTO createProperty(CreatePropertyRequestDTO requestDTO);
    List<PropertyResponseDTO> getAllProperties();
    Optional<PropertyResponseDTO> getPropertyById(Long id);
    PropertyResponseDTO updateProperty(Long id, CreatePropertyRequestDTO requestDTO);
    void deleteProperty(Long id);
}
