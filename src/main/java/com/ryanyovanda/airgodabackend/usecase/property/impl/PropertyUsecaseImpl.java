package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.*;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreatePropertyRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.LocationDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PropertyResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.*;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.cloudinary.CloudinaryUsecase;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import jakarta.transaction.Transactional;
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
    private final UsersRepository usersRepository;

    @Autowired
    public PropertyUsecaseImpl(
            PropertyRepository propertyRepository,
            PropertyCategoryRepository propertyCategoryRepository,
            LocationRepository locationRepository,
            PropertyImageRepository propertyImageRepository,
            CloudinaryUsecase cloudinaryUsecase,
            UsersRepository usersRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.locationRepository = locationRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.cloudinaryUsecase = cloudinaryUsecase;
        this.usersRepository = usersRepository;
    }

    @Override
    public PropertyResponseDTO createProperty(CreatePropertyRequestDTO requestDTO, List<MultipartFile> images) {
        Property property = new Property();
        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setFullAddress(requestDTO.getFullAddress());
        property.setRoomId(requestDTO.getRoomId());
        property.setIsActive(true);

        // ✅ Fetch and set tenant
        if (requestDTO.getTenantId() != null) {
            User tenant = usersRepository.findById(requestDTO.getTenantId())
                    .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
            property.setTenant(tenant);
        } else {
            throw new IllegalArgumentException("Tenant must be specified");
        }

        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            property.setCategory(category);
        }

        if (requestDTO.getLocationId() != null) {
            Location location = validateAndGetLocation(requestDTO.getLocationId());
            property.setLocation(location);
        }

        Property savedProperty = propertyRepository.save(property);

        // Handle image uploads
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
    public void deletePropertyImage(Long imageId) {
        propertyImageRepository.deleteById(imageId);
    }

    @Override
    public Optional<PropertyResponseDTO> getPropertyById(Long id) {
        return propertyRepository.findById(id).map(this::mapToResponseDTO);
    }

    @Override
    public List<PropertyResponseDTO> getPropertiesByTenant(Long tenantId) {
        List<Property> properties = propertyRepository.findByOwnerId(tenantId);

        return properties.stream().map(property -> {
            PropertyResponseDTO dto = new PropertyResponseDTO();
            dto.setId(property.getId());
            dto.setName(property.getName());
            dto.setDescription(property.getDescription());
            dto.setIsActive(property.getIsActive());
            dto.setCreatedAt(property.getCreatedAt());
            dto.setCategoryId(property.getCategory().getId()); // Assuming category exists
            dto.setLocation(new LocationDTO(
                    property.getLocation().getId(),
                    property.getLocation().getName(),
                    property.getLocation().getType().name()
            ));
            dto.setImageUrls(property.getImages().stream().map(PropertyImage::getImageUrl).toList()); // Assuming images exist
            dto.setFullAddress(property.getFullAddress());

            return dto;
        }).collect(Collectors.toList());
    }



    @Override
    @Transactional
    public PropertyResponseDTO updatePropertyImages(Long propertyId, List<MultipartFile> images) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // Ensure transaction when deleting images
        propertyImageRepository.deleteByPropertyId(propertyId);

        // Upload new images
        List<String> imageUrls = cloudinaryUsecase.uploadImages(images);
        List<PropertyImage> propertyImages = imageUrls.stream().map(url -> {
            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setImageUrl(url);
            propertyImage.setProperty(property);
            return propertyImage;
        }).collect(Collectors.toList());


        propertyImageRepository.saveAll(propertyImages);

        return mapToResponseDTO(property);
    }

    @Override
    public PropertyResponseDTO updateProperty(Long id, CreatePropertyRequestDTO requestDTO) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        property.setName(requestDTO.getName());
        property.setDescription(requestDTO.getDescription());
        property.setRoomId(requestDTO.getRoomId());

        if (requestDTO.getCategoryId() != null) {
            PropertyCategory category = propertyCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            property.setCategory(category);
        }

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

    @Override
    public List<PropertyResponseDTO> getAllProperties() {
        return propertyRepository.findByIsActiveTrue()
                .stream().map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

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
    public Page<PropertyResponseDTO> getPropertiesByCategorySortedByCheapest(Long categoryId, Pageable pageable) {
        return propertyRepository.findByCategorySortedByCheapestPrice(categoryId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocationAndCategorySortedByCheapest(Long locationId, Long categoryId, Pageable pageable) {
        return propertyRepository.findByLocationAndCategorySortedByCheapestPrice(locationId, categoryId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesByLocationSortedByCheapest(Long locationId, Pageable pageable) {
        return propertyRepository.findByLocationSortedByCheapestPrice(locationId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<PropertyResponseDTO> getPropertiesSortedByCheapestPrice(Pageable pageable) {
        return propertyRepository.findAllSortedByCheapestPrice(pageable)
                .map(this::mapToResponseDTO);
    }

    private Location validateAndGetLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        if (location.getType() != Location.LocationType.CITY && location.getType() != Location.LocationType.REGENCY) {
            throw new IllegalArgumentException("Property must be assigned to a city or regency");
        }
        return location;
    }

    private PropertyResponseDTO mapToResponseDTO(Property property) {
        PropertyResponseDTO responseDTO = new PropertyResponseDTO();
        responseDTO.setId(property.getId());
        responseDTO.setName(property.getName());
        responseDTO.setDescription(property.getDescription());
        responseDTO.setFullAddress(property.getFullAddress());
        responseDTO.setRoomId(property.getRoomId());
        responseDTO.setIsActive(property.getIsActive());

        // ✅ Null-safe handling for tenant
        if (property.getTenant() != null) {
            responseDTO.setTenantId(property.getTenant().getId());
        } else {
            responseDTO.setTenantId(null);  // Avoid NullPointerException
        }

        if (property.getCategory() != null) {
            responseDTO.setCategoryId(property.getCategory().getId());
        }

        if (property.getLocation() != null) {
            responseDTO.setLocation(new LocationDTO(
                    property.getLocation().getId(),
                    property.getLocation().getName(),
                    property.getLocation().getType().toString()
            ));
        }

        List<String> imageUrls = property.getImages() != null
                ? property.getImages().stream().map(PropertyImage::getImageUrl).toList()
                : new ArrayList<>();
        responseDTO.setImageUrls(imageUrls);

        return responseDTO;
    }
}