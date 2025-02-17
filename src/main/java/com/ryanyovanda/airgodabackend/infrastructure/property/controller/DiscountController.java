package com.ryanyovanda.airgodabackend.infrastructure.property.controller;

import com.ryanyovanda.airgodabackend.entity.Discount;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountRepository discountRepository;

    // ✅ Add Discount
    @PostMapping
    public ResponseEntity<String> addDiscount(@RequestBody Discount discount) {
        discountRepository.save(discount);
        return ResponseEntity.ok("Discount added successfully.");
    }

    // ✅ Get All Discounts
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountRepository.findAll());
    }
}
