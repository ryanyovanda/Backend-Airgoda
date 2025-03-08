package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.*;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.LocationDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.*;
import com.ryanyovanda.airgodabackend.usecase.cloudinary.CloudinaryUsecase;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertyUsecaseImpl implements PropertyUsecase {

    private final PropertyRepository propertyRepository;
    private final PropertyCategoryRepository propertyCategoryRepository;
    private final LocationRepository locationRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final CloudinaryUsecase cloudinaryUsecase;

    @Autowired
    public PropertyUsecaseImpl(
            PropertyRepository propertyRepository,
            PropertyCategoryRepository propertyCategoryRepository,
            LocationRepository locationRepository,
            PropertyImageRepository propertyImageRepository,
            CloudinaryUsecase cloudinaryUsecase) {
        this.propertyRepository = propertyRepository;
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.locationRepository = locationRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.cloudinaryUsecase = cloudinaryUsecase;
    }

    @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO requestDTO, List<MultipartFile> images) {
        Property property = new Property();
        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setRoomId(requestDTO.getRoomId());
        property.setIsActive(true);

        // Assign category
        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            property.setCategory(category);
        }

        // Assign location
        if (requestDTO.getLocationId() != null) {
            Location location = validateAndGetLocation(requestDTO.getLocationId());
            property.setLocation(location);
        }

        // Save property first to get the ID
        Property savedProperty = propertyRepository.save(property);

        // Upload images and save to PropertyImage table
        List<String> imageUrls = cloudinaryUsecase.uploadImages(images);
        List<PropertyImage> propertyImages = imageUrls.stream().map(url -> {
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageUrl(url);
            propertyImage.setProperty(savedProperty);
            return propertyImage;
        }).collect(Collectors.toList());

        propertyImageRepository.saveAll(propertyImages);

        return mapToResponseDTO(savedProperty);
    }

    @Override
    public List<PropertyResponseDTO> getAllProperties() {
        return propertyRepository.findByIsActiveTrue()
                .stream().map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PropertyResponseDTO> getPropertyById(Long id) {
        return propertyRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Override
    public PropertyResponseDTO updateProperty(Long id, CreatePropertyRequestDTO requestDTO) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setRoomId(requestDTO.getRoomId());

        // Update category
        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            property.setCategory(category);
        }

        // Update location
        if (requestDTO.getLocationId() != null) {
            Location location = validateAndGetLocation(requestDTO.getLocationId());
            property.setLocation(location);
        }

        Property updatedProperty = propertyRepository.save(property);
        return mapToResponseDTO(updatedProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("Property not found");
        }
        propertyRepository.deleteById(id);
    }

    // Pagination & Filtering Implementations

    @Override
    public Page<PropertyResponseDTO> getProperties(Pageable pageable) {
        return propertyRepository.findByIsActiveTrue(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocation(Long locationId, Pageable pageable) {
        return propertyRepository.findByLocationIdAndIsActiveTrue(locationId, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByCategory(Long categoryId, Pageable pageable) {
        return propertyRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocationAndCategory(Long locationId, Long categoryId, Pageable pageable) {
        return propertyRepository.findByLocationIdAndCategoryIdAndIsActiveTrue(locationId, categoryId, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesSortedByCheapestPrice(Pageable pageable) {
        return propertyRepository.findAllSortedByCheapestPrice(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocationSortedByCheapest(Long locationId, Pageable pageable) {
        return propertyRepository.findByLocationSortedByCheapestPrice(locationId, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByCategorySortedByCheapest(Long categoryId, Pageable pageable) {
        return propertyRepository.findByCategorySortedByCheapestPrice(categoryId, pageable).map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocationAndCategorySortedByCheapest(Long locationId, Long categoryId, Pageable pageable) {
        return propertyRepository.findByLocationAndCategorySortedByCheapestPrice(locationId, categoryId, pageable).map(this::mapToResponseDTO);
    }

    // Helper Method: Validate and Get Location
    private Location validateAndGetLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        if (location.getType() != Location.LocationType.CITY && location.getType() != Location.LocationType.REGENCY) {
            throw new IllegalArgumentException("Property must be assigned to a city or regency");
        }
        return location;
    }

    // Helper Method: Convert Property to DTO
    private PropertyResponseDTO mapToResponseDTO(Property property) {
        PropertyResponseDTO responseDTO = new PropertyResponseDTO();
        responseDTO.setId(property.getId());
        responseDTO.setName(property.getName());
        responseDTO.setDescription(property.getDescription());
        responseDTO.setRoomId(property.getRoomId());
        responseDTO.setIsActive(property.getIsActive());
        responseDTO.setCategoryId(property.getCategory() != null ? property.getCategory().getId() : null);

        if (property.getLocation() != null) {
            responseDTO.setLocation(new LocationDTO(
                    property.getLocation().getId(),
                    property.getLocation().getName(),
                    property.getLocation().getType().toString()
            ));
        }

        // Set image URLs
        List<String> imageUrls = property.getImages() != null
                ? property.getImages().stream().map(PropertyImage::getImageUrl).toList()
                : new ArrayList<>();

        responseDTO.setImageUrls(imageUrls);

        return responseDTO;
    }
}
