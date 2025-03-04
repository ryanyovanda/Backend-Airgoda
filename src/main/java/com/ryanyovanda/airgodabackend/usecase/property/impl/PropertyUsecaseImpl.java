package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.Property;
import com.ryanyovanda.airgodabackend.entity.PropertyCategory;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyCategoryRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyRepository;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertyUsecaseImpl implements PropertyUsecase {
    private final PropertyRepository propertyRepository;
    private final PropertyCategoryRepository propertyCategoryRepository;

    @Autowired
    public PropertyUsecaseImpl(PropertyRepository propertyRepository, PropertyCategoryRepository propertyCategoryRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyCategoryRepository = propertyCategoryRepository;
    }

    @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO requestDTO) {
        Property property = new Property();
        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setRoomId(requestDTO.getRoomId());

        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            property.setCategory(category);
        }

        Property savedProperty = propertyRepository.save(property);
        return mapToResponseDTO(savedProperty);
    }

    @Override
    public List<PropertyResponseDTO> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<PropertyResponseDTO> getPropertyById(Long id) {
        return propertyRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Override
    public PropertyResponseDTO updateProperty(Long id, CreatePropertyRequestDTO requestDTO) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setRoomId(requestDTO.getRoomId());

        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            property.setCategory(category);
        }

        Property updatedProperty = propertyRepository.save(property);
        return mapToResponseDTO(updatedProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new RuntimeException("Property not found");
        }
        propertyRepository.deleteById(id);
    }

    private PropertyResponseDTO mapToResponseDTO(Property property) {
        PropertyResponseDTO responseDTO = new PropertyResponseDTO();
        responseDTO.setId(property.getId());
        responseDTO.setName(property.getName());
        responseDTO.setDescription(property.getDescription());
        responseDTO.setRoomId(property.getRoomId());
        responseDTO.setCategoryId(property.getCategory() != null ? property.getCategory().getId() : null);
        return responseDTO;
    }
}
