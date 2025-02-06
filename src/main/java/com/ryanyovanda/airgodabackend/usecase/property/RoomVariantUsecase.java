package com.ryanyovanda.airgodabackend.usecase.property;


import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import java.util.List;
import java.util.Optional;

public interface RoomVariantUsecase {
    RoomVariant createRoomVariant(RoomVariant roomVariant); // Tambah room variant baru
    Optional<RoomVariant> getRoomVariantById(Long id); // Ambil room variant berdasarkan ID
    List<RoomVariant> getAllRoomVariants(); // Ambil semua room variant
    List<RoomVariant> getRoomVariantsByPropertyId(Long propertyId); // Ambil room variant berdasarkan property
    RoomVariant updateRoomVariant(Long id, RoomVariant roomVariant); // Update room variant
    void deleteRoomVariant(Long id); // Soft delete room variant
}
