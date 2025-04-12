package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.entity.PeakRate;
import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PeakRateResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.property.dto.PeakRateRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.PeakRateRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/peak-rates")
public class PeakRateController {

    private static final Logger logger = Logger.getLogger(PeakRateController.class.getName());

    @Autowired
    private PeakRateRepository peakRateRepository;

    @Autowired
    private RoomVariantRepository roomVariantRepository;

    @PreAuthorize("hasAuthority('SCOPE_TENANT')")
    @PostMapping
    @Transactional
    public ResponseEntity<?> addPeakRate(@RequestBody PeakRateRequestDTO peakRateRequest) {
        try {
            logger.info("🔍 Received Peak Rate Request: " + peakRateRequest);

            Long roomVariantId = peakRateRequest.getRoomVariantId();

            if (roomVariantId == null) {
                logger.warning(" Error: `roomVariantId` is missing.");
                return ResponseEntity.badRequest().body("Error: `roomVariantId` is required.");
            }

            Optional<RoomVariant> roomVariantOpt = roomVariantRepository.findById(roomVariantId);
            if (roomVariantOpt.isEmpty()) {
                logger.warning(" Error: Room Variant ID " + roomVariantId + " not found.");
                return ResponseEntity.badRequest().body("Error: Room Variant with ID " + roomVariantId + " not found.");
            }


            PeakRate peakRate = new PeakRate();
            peakRate.setRoomVariant(roomVariantOpt.get());
            peakRate.setStartDate(peakRateRequest.getStartDate());
            peakRate.setEndDate(peakRateRequest.getEndDate());
            peakRate.setAdditionalPrice(peakRateRequest.getAdditionalPrice());


            boolean exists = peakRateRepository.existsByRoomVariantIdAndDateRange(
                    roomVariantId, peakRateRequest.getStartDate(), peakRateRequest.getEndDate());

            if (exists) {
                logger.warning("️ Peak Rate already exists for Room Variant ID: " + roomVariantId);
                return ResponseEntity.badRequest().body("Peak Rate already exists for the given date range.");
            }


            PeakRate savedPeakRate = peakRateRepository.save(peakRate);
            logger.info(" Peak Rate saved successfully: " + savedPeakRate);

            return ResponseEntity.ok("Peak Rate added successfully.");
        } catch (Exception e) {
            logger.severe(" Failed to add Peak Rate: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to add Peak Rate: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<PeakRateResponseDTO>> getAllPeakRates() {
        List<PeakRateResponseDTO> response = peakRateRepository.findAll()
                .stream()
                .map(peakRate -> {
                    PeakRateResponseDTO dto = new PeakRateResponseDTO();
                    dto.setId(peakRate.getId());
                    dto.setRoomVariantId(peakRate.getRoomVariant().getId());
                    dto.setStartDate(peakRate.getStartDate());
                    dto.setEndDate(peakRate.getEndDate());
                    dto.setAdditionalPrice(peakRate.getAdditionalPrice());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_TENANT')")
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deletePeakRate(@PathVariable Long id) {
        try {
            Optional<PeakRate> peakRateOpt = peakRateRepository.findById(id);

            if (peakRateOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Peak Rate not found with ID: " + id);
            }

            peakRateRepository.deleteById(id);
            return ResponseEntity.ok("Peak Rate deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete Peak Rate: " + e.getMessage());
        }
    }
}
