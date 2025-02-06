package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.entity.Property;

import java.util.List;
import java.util.Optional;

public interface PropertyUsecase {
    Property createProperty(Property property);
    Optional<Property> getPropertyById(Long id);
    List<Property> getAllProperties();
    Property updateProperty(Long id, Property property);
    void deleteProperty(Long id);
}
