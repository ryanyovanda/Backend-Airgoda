package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.usecase.property.RoomVariantUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/room-variants")
public class RoomVariantController {

    private final RoomVariantUsecase roomVariantUseCase;

    @Autowired
    public RoomVariantController(RoomVariantUsecase roomVariantUseCase) {
        this.roomVariantUseCase = roomVariantUseCase;
    }

    /**
     * Create Room Variant
     */
    @PostMapping
    public ResponseEntity<RoomVariant> createRoomVariant(@RequestBody RoomVariant roomVariant) {
        RoomVariant savedRoomVariant = roomVariantUseCase.createRoomVariant(roomVariant);
        return ResponseEntity.ok(savedRoomVariant);
    }

    /**
     * Get All Room Variants
     */
    @GetMapping
    public ResponseEntity<List<RoomVariant>> getAllRoomVariants() {
        List<RoomVariant> roomVariants = roomVariantUseCase.getAllRoomVariants();
        return ResponseEntity.ok(roomVariants);
    }

    /**
     * Get Room Variant By ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomVariant> getRoomVariantById(@PathVariable Long id) {
        Optional<RoomVariant> roomVariant = roomVariantUseCase.getRoomVariantById(id);
        return roomVariant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get Room Variants By Property ID
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<RoomVariant>> getRoomVariantsByPropertyId(@PathVariable Long propertyId) {
        List<RoomVariant> roomVariants = roomVariantUseCase.getRoomVariantsByPropertyId(propertyId);
        return ResponseEntity.ok(roomVariants);
    }

    /**
     * Update Room Variant
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomVariant> updateRoomVariant(@PathVariable Long id, @RequestBody RoomVariant roomVariant) {
        RoomVariant updatedRoomVariant = roomVariantUseCase.updateRoomVariant(id, roomVariant);
        return ResponseEntity.ok(updatedRoomVariant);
    }

    /**
     * Delete Room Variant (Soft Delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomVariant(@PathVariable Long id) {
        roomVariantUseCase.deleteRoomVariant(id);
        return ResponseEntity.noContent().build();
    }
}
