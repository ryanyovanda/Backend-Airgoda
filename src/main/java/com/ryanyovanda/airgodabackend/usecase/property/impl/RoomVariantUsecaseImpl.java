package com.ryanyovanda.airgodabackend.usecase.property.impl;

import com.ryanyovanda.airgodabackend.entity.Property;
import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.PropertyRepository;
import com.ryanyovanda.airgodabackend.usecase.property.RoomVariantUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomVariantUsecaseImpl implements RoomVariantUsecase {

    @Autowired
    private RoomVariantRepository roomVariantRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Override
    public GetRoomVariantDTO createRoomVariant(CreateRoomVariantDTO dto) {
        // Cari Property berdasarkan ID dari DTO
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Inisialisasi RoomVariant sebelum digunakan
        RoomVariant roomVariant = new RoomVariant();
        roomVariant.setName(dto.getName());
        roomVariant.setPrice(dto.getPrice());
        roomVariant.setMaxGuest(dto.getCapacity());
        roomVariant.setFacilities(dto.getFacilities() != null ? dto.getFacilities() : new ArrayList<>());
        roomVariant.setProperty(property); // Pastikan property tidak null

        // Simpan RoomVariant ke database
        roomVariant = roomVariantRepository.save(roomVariant);

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
        return roomVariantRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<GetRoomVariantDTO> getRoomVariantsByPropertyId(Long propertyId) {
        return roomVariantRepository.findByPropertyId(propertyId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public GetRoomVariantDTO updateRoomVariant(Long id, UpdateRoomVariantDTO dto) {
        RoomVariant roomVariant = roomVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room variant not found"));

        if (dto.getName() != null) roomVariant.setName(dto.getName());
        if (dto.getPrice() != null) roomVariant.setPrice(dto.getPrice());
        if (dto.getCapacity() != null) roomVariant.setMaxGuest(dto.getCapacity());
        if (dto.getFacilities() != null) roomVariant.setFacilities(dto.getFacilities());

        roomVariant = roomVariantRepository.save(roomVariant);
        return mapToDTO(roomVariant);
    }

    @Override
    public void deleteRoomVariant(Long id) {
        if (!roomVariantRepository.existsById(id)) {
            throw new RuntimeException("Room variant not found");
        }
        roomVariantRepository.deleteById(id);
    }

    private GetRoomVariantDTO mapToDTO(RoomVariant roomVariant) {
        GetRoomVariantDTO dto = new GetRoomVariantDTO();
        dto.setId(roomVariant.getId());
        dto.setName(roomVariant.getName());
        dto.setPrice(roomVariant.getPrice());
        dto.setCapacity(roomVariant.getMaxGuest());
        dto.setFacilities(String.join(",", roomVariant.getFacilities()));
        dto.setPropertyId(roomVariant.getProperty().getId());
        return dto;
    }
}