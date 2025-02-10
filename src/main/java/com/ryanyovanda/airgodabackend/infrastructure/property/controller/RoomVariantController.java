package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.infrastructure.property.dto.CreateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.UpdateRoomVariantDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.GetRoomVariantDTO;
import com.ryanyovanda.airgodabackend.usecase.property.RoomVariantUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-variants")
public class RoomVariantController {

    @Autowired
    private RoomVariantUsecase roomVariantUsecase;

    @PostMapping
    public ResponseEntity<GetRoomVariantDTO> createRoomVariant(@RequestBody CreateRoomVariantDTO dto) {
        return ResponseEntity.ok(roomVariantUsecase.createRoomVariant(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetRoomVariantDTO> getRoomVariantById(@PathVariable Long id) {
        return ResponseEntity.ok(roomVariantUsecase.getRoomVariantById(id));
    }

    @GetMapping
    public ResponseEntity<List<GetRoomVariantDTO>> getAllRoomVariants() {
        return ResponseEntity.ok(roomVariantUsecase.getAllRoomVariants());
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<GetRoomVariantDTO>> getRoomVariantsByPropertyId(@PathVariable Long propertyId) {
        return ResponseEntity.ok(roomVariantUsecase.getRoomVariantsByPropertyId(propertyId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetRoomVariantDTO> updateRoomVariant(@PathVariable Long id, @RequestBody UpdateRoomVariantDTO dto) {
        return ResponseEntity.ok(roomVariantUsecase.updateRoomVariant(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomVariant(@PathVariable Long id) {
        roomVariantUsecase.deleteRoomVariant(id);
        return ResponseEntity.noContent().build();
    }
}
