package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.entity.PeakRate;
import com.ryanyovanda.airgodabackend.entity.RoomVariant;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.PeakRateRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/peak-rates")
public class PeakRateController {

    private static final Logger logger = Logger.getLogger(PeakRateController.class.getName());

    @Autowired
    private PeakRateRepository peakRateRepository;

    @Autowired
    private RoomVariantRepository roomVariantRepository;

    // ✅ Add Peak Rate with Logging & Validation
    @PostMapping
    @Transactional
    public ResponseEntity<?> addPeakRate(@RequestBody PeakRate peakRate) {
        try {
            logger.info("🔍 Received Peak Rate Request: " + peakRate.toString());

            // 🔥 Pastikan `roomVariant` valid
            Long roomVariantId = peakRate.getRoomVariant().getId();
            Optional<RoomVariant> roomVariantOpt = roomVariantRepository.findById(roomVariantId);

            if (roomVariantOpt.isEmpty()) {
                logger.warning("❌ Error: Room Variant ID " + roomVariantId + " not found.");
                return ResponseEntity.badRequest().body("Error: Room Variant with ID " + roomVariantId + " not found.");
            }

            peakRate.setRoomVariant(roomVariantOpt.get());

            // ✅ Simpan Peak Rate
            PeakRate savedPeakRate = peakRateRepository.save(peakRate);
            logger.info("✅ Peak Rate saved successfully: " + savedPeakRate.toString());

            return ResponseEntity.ok("Peak Rate added successfully.");
        } catch (Exception e) {
            logger.severe("❌ Failed to add Peak Rate: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to add Peak Rate: " + e.getMessage());
        }
    }

    // ✅ Get All Peak Rates
    @GetMapping
    public ResponseEntity<List<PeakRate>> getAllPeakRates() {
        return ResponseEntity.ok(peakRateRepository.findAll());
    }
}
