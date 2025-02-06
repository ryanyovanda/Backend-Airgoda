package com.ryanyovanda.airgodabackend.usecase.property.impl;


import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import com.ryanyovanda.airgodabackend.usecase.property.RoomVariantUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomVariantUsecaseImpl implements RoomVariantUsecase {

    private final RoomVariantRepository roomVariantRepository;

    @Autowired
    public RoomVariantUsecaseImpl(RoomVariantRepository roomVariantRepository) {
        this.roomVariantRepository = roomVariantRepository;
    }

    @Override
    public RoomVariant createRoomVariant(RoomVariant roomVariant) {
        return roomVariantRepository.save(roomVariant);
    }

    @Override
    public Optional<RoomVariant> getRoomVariantById(Long id) {
        return roomVariantRepository.findById(id);
    }

    @Override
    public List<RoomVariant> getAllRoomVariants() {
        return roomVariantRepository.findAll();
    }

    @Override
    public List<RoomVariant> getRoomVariantsByPropertyId(Long propertyId) {
        return roomVariantRepository.findByPropertyId(propertyId);
    }

    @Override
    public RoomVariant updateRoomVariant(Long id, RoomVariant roomVariant) {
        return roomVariantRepository.findById(id).map(existingVariant -> {
            existingVariant.setName(roomVariant.getName());
            existingVariant.setDescription(roomVariant.getDescription());
            existingVariant.setMaxGuest(roomVariant.getMaxGuest());
            existingVariant.setPrice(roomVariant.getPrice());
            return roomVariantRepository.save(existingVariant);
        }).orElseThrow(() -> new RuntimeException("Room Variant not found"));
    }

    @Override
    public void deleteRoomVariant(Long id) {
        roomVariantRepository.findById(id).ifPresent(roomVariant -> {
            roomVariantRepository.delete(roomVariant);
        });
    }
}
