package com.ryanyovanda.airgodabackend.usecase.property;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;

import java.util.List;

public interface RoomVariantUsecase {
    GetRoomVariantDTO createRoomVariant(CreateRoomVariantDTO roomVariantDTO, Long tenantId);
    GetRoomVariantDTO getRoomVariantById(Long id);
    List<GetRoomVariantDTO> getAllRoomVariants();
    List<GetRoomVariantDTO> getRoomVariantsByPropertyId(Long propertyId);
    GetRoomVariantDTO updateRoomVariant(Long id, UpdateRoomVariantDTO roomVariantDTO, Long tenantId);
    void deleteRoomVariant(Long id, Long tenantId);
}
