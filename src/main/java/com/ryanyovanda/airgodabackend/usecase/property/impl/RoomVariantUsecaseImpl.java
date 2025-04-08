package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.Property;
import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyRepository;
import com.ryanyovanda.airgodabackend.usecase.property.RoomVariantUsecase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomVariantUsecaseImpl implements RoomVariantUsecase {

    @Autowired
    private RoomVariantRepository roomVariantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public GetRoomVariantDTO createRoomVariant(CreateRoomVariantDTO dto, Long currentTenantId) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (!property.getTenant().getId().equals(currentTenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this property.");
        }

        RoomVariant roomVariant = new RoomVariant();
        roomVariant.setName(dto.getName());
        roomVariant.setPrice(dto.getPrice());
        roomVariant.setMaxGuest(dto.getMaxGuest());
        roomVariant.setCapacity(dto.getCapacity());
        roomVariant.setFacilities(dto.getFacilities() != null ? dto.getFacilities() : new ArrayList<>());
        roomVariant.setProperty(property);

        if (roomVariant.getCreatedAt() == null) {
            roomVariant.setCreatedAt(OffsetDateTime.now());
        }
        roomVariant.setUpdatedAt(OffsetDateTime.now());

        roomVariant = roomVariantRepository.save(roomVariant);

        entityManager.flush();
        entityManager.clear();

        return mapToDTO(roomVariant);
    }

    @Override
    public GetRoomVariantDTO getRoomVariantById(Long id) {
        RoomVariant roomVariant = roomVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room variant not found"));
        return mapToDTO(roomVariant);
    }

    @Override
    public List<GetRoomVariantDTO> getAllRoomVariants() {
        return roomVariantRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GetRoomVariantDTO updateRoomVariant(Long id, UpdateRoomVariantDTO dto, Long tenantId) {
        RoomVariant roomVariant = roomVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room variant not found"));
        Property property = roomVariant.getProperty();
        if (property == null || !property.getTenant().getId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this property.");
        }

        if (dto.getName() != null) roomVariant.setName(dto.getName());
        if (dto.getPrice() != null) roomVariant.setPrice(dto.getPrice());
        if (dto.getMaxGuest() != null) roomVariant.setMaxGuest(dto.getMaxGuest());
        if (dto.getCapacity() != null) roomVariant.setCapacity(dto.getCapacity());
        if (dto.getFacilities() != null) roomVariant.setFacilities(dto.getFacilities());

        roomVariant.setUpdatedAt(OffsetDateTime.now());

        roomVariant = roomVariantRepository.save(roomVariant);

        return mapToDTO(roomVariant);
    }

    @Override
    public List<GetRoomVariantDTO> getRoomVariantsByPropertyId(Long propertyId) {
        return roomVariantRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRoomVariant(Long id, Long tenantId) {
        RoomVariant roomVariant = roomVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room variant not found"));
        Property property = roomVariant.getProperty();
        if (property == null || !property.getTenant().getId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this property.");
        }

        roomVariantRepository.delete(roomVariant);

        entityManager.flush();
    }

    private GetRoomVariantDTO mapToDTO(RoomVariant roomVariant) {
        GetRoomVariantDTO dto = new GetRoomVariantDTO();
        dto.setId(roomVariant.getId());
        dto.setName(roomVariant.getName());
        dto.setPrice(roomVariant.getPrice());
        dto.setMaxGuest(roomVariant.getMaxGuest());
        dto.setCapacity(roomVariant.getCapacity());
        dto.setFacilities(roomVariant.getFacilities());
        dto.setPropertyId(roomVariant.getProperty().getId());
        return dto;
    }
}
