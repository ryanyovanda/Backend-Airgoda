package com.ryanyovanda.airgodabackend.infrastructure.order.controller;

import com.ryanyovanda.airgodabackend.entity.Order;
import com.ryanyovanda.airgodabackend.usecase.booking.BookingUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private BookingUsecase bookingUsecase;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            logger.info("Received order request: {}", order);
            bookingUsecase.createBooking(order);
            logger.info("Order successfully created!");
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\": \"Order created successfully.\"}");
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Failed to create order: " + e.getMessage() + "\"}");
        }
    }
}
