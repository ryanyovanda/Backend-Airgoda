package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;

import java.util.List;

public interface RoomVariantUsecase {
    GetRoomVariantDTO createRoomVariant(CreateRoomVariantDTO roomVariantDTO);
    GetRoomVariantDTO getRoomVariantById(Long id);
    List<GetRoomVariantDTO> getAllRoomVariants();
    List<GetRoomVariantDTO> getRoomVariantsByPropertyId(Long propertyId);
    GetRoomVariantDTO updateRoomVariant(Long id, UpdateRoomVariantDTO roomVariantDTO);
    void deleteRoomVariant(Long id);
}
