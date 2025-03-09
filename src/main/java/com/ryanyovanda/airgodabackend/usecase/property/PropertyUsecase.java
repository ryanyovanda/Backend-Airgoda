package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PropertyUsecase {
    PropertyResponseDTO createProperty(CreatePropertyRequestDTO requestDTO, List<MultipartFile> images);
    List<PropertyResponseDTO> getAllProperties();
    Optional<PropertyResponseDTO> getPropertyById(Long id);
    PropertyResponseDTO updateProperty(Long id, CreatePropertyRequestDTO requestDTO);
    void deleteProperty(Long id);
    void deletePropertyImage(Long imageId);
    PropertyResponseDTO updatePropertyImages(Long propertyId, List<MultipartFile> images);

    // Pagination & Filtering
    Page<PropertyResponseDTO> getProperties(Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByLocation(Long locationId, Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByCategory(Long categoryId, Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByLocationAndCategory(Long locationId, Long categoryId, Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesSortedByCheapestPrice(Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByLocationSortedByCheapest(Long locationId, Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByCategorySortedByCheapest(Long categoryId, Pageable pageable);
    Page<PropertyResponseDTO> getPropertiesByLocationAndCategorySortedByCheapest(Long locationId, Long categoryId, Pageable pageable);
}
