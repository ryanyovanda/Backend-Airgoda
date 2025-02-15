package com.ryanyovanda.airgodabackend.usecase.booking.impl;

import com.ryanyovanda.airgodabackend.entity.*;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.OrderItemRepository;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.OrderRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.RoomVariantRepository;
import com.ryanyovanda.airgodabackend.infrastructure.property.repository.DiscountRepository;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.PeakRateRepository;
import com.ryanyovanda.airgodabackend.usecase.booking.BookingUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class BookingUsecaseImpl implements BookingUsecase {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RoomVariantRepository roomVariantRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private PeakRateRepository peakRateRepository;

    @Transactional
    @Override
    public void createBooking(Order order) {
        BigDecimal totalOrderPrice = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            Long roomVariantId = item.getRoomVariant().getId();

            // ✅ Ensure RoomVariant exists
            Optional<RoomVariant> optionalRoomVariant = roomVariantRepository.findById(roomVariantId);
            if (optionalRoomVariant.isEmpty()) {
                throw new IllegalArgumentException("Room Variant with ID " + roomVariantId + " does not exist.");
            }
            RoomVariant roomVariant = optionalRoomVariant.get();

            // ✅ Ensure base price is not null
            BigDecimal finalPrice = roomVariant.getPrice();
            if (finalPrice == null) {
                finalPrice = BigDecimal.ZERO;
            }

            // ✅ Apply discount (if applicable)
            Discount discount = discountRepository.findValidDiscount(roomVariant.getId(), item.getStartDate(), item.getEndDate());
            if (discount != null) {
                if (discount.getDiscountType().equalsIgnoreCase("percentage")) {
                    finalPrice = finalPrice.subtract(finalPrice.multiply(discount.getValue().divide(BigDecimal.valueOf(100))));
                } else {
                    finalPrice = finalPrice.subtract(discount.getValue());
                }
            }

            // ✅ Apply peak rate (if applicable)
            PeakRate peakRate = peakRateRepository.findValidPeakRate(roomVariant.getId(), item.getStartDate(), item.getEndDate());
            if (peakRate != null) {
                finalPrice = finalPrice.add(peakRate.getAdditionalPrice());
            }

            // ✅ Ensure finalPrice is never negative
            if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
                finalPrice = BigDecimal.ZERO;
            }

            // ✅ Calculate the number of nights (at least 1)
            long nights = ChronoUnit.DAYS.between(item.getStartDate(), item.getEndDate());
            if (nights < 1) nights = 1;

            // ✅ Calculate total price per item (HANYA BERDASARKAN LAMA MENGINAP)
            BigDecimal totalItemPrice = finalPrice.multiply(BigDecimal.valueOf(nights));

            // ✅ Set the calculated total price
            item.setTotalPrice(totalItemPrice);

            // ✅ Update total order price
            totalOrderPrice = totalOrderPrice.add(totalItemPrice);

            // ✅ Save order item
            orderItemRepository.save(item);
        }

        // ✅ Set total price to order before saving
        order.setTotalPrice(totalOrderPrice);
        orderRepository.save(order);
    }
}
