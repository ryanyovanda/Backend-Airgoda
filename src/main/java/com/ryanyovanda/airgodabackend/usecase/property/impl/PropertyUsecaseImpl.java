package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.Property;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyRepository;
import com.ryanyovanda.airgodabackend.usecase.property.PropertyUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyUsecaseImpl implements PropertyUsecase {
    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyUsecaseImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    @Override
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    @Override
    public List<Property> getAllProperties(){
        return propertyRepository.findByIsActiveTrue();
    }

    @Override
    public Property updateProperty(Long id, Property property) {
        return propertyRepository.findById(id).map(existingProperty -> {
            existingProperty.setName(property.getName());
            existingProperty.setDescription(property.getDescription());
            existingProperty.setCategory(property.getCategory());
            return propertyRepository.save(existingProperty);
        }).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    @Override
    public void deleteProperty(Long id) {
        propertyRepository.findById(id).ifPresent(property -> {
            property.setIsDeleted(true);
            propertyRepository.save(property);
        });
    }
}
