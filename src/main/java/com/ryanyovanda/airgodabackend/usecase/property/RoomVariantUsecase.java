package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;

import java.util.List;

public interface RoomVariantUsecase {
    GetRoomVariantDTO createRoomVariant(CreateRoomVariantDTO roomVariantDTO); // Tambah room variant baru
    GetRoomVariantDTO getRoomVariantById(Long id); // Ambil room variant berdasarkan ID
    List<GetRoomVariantDTO> getAllRoomVariants(); // Ambil semua room variant
    List<GetRoomVariantDTO> getRoomVariantsByPropertyId(Long propertyId); // Ambil room variant berdasarkan property
    GetRoomVariantDTO updateRoomVariant(Long id, UpdateRoomVariantDTO roomVariantDTO); // Update room variant
    void deleteRoomVariant(Long id); // Hapus room variant
}
