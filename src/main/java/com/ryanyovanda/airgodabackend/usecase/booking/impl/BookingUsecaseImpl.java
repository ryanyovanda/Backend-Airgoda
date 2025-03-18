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
import java.time.LocalDate;
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

            Optional<RoomVariant> optionalRoomVariant = roomVariantRepository.findById(roomVariantId);
            if (optionalRoomVariant.isEmpty()) {
                throw new IllegalArgumentException("Room Variant with ID " + roomVariantId + " does not exist.");
            }
            RoomVariant roomVariant = optionalRoomVariant.get();

            BigDecimal totalItemPrice = BigDecimal.ZERO;
            LocalDate currentDate = item.getStartDate();

            while (!currentDate.isEqual(item.getEndDate())) {
                BigDecimal nightlyPrice = roomVariant.getPrice();

                Discount discount = discountRepository.findValidDiscount(roomVariant.getId(), currentDate, currentDate);
                if (discount != null) {
                    if (discount.getDiscountType().equalsIgnoreCase("percentage")) {
                        nightlyPrice = nightlyPrice.subtract(nightlyPrice.multiply(discount.getValue().divide(BigDecimal.valueOf(100))));
                    } else {
                        nightlyPrice = nightlyPrice.subtract(discount.getValue());
                    }
                }

                PeakRate peakRate = peakRateRepository.findValidPeakRate(roomVariant.getId(), currentDate, currentDate);
                if (peakRate != null) {
                    nightlyPrice = nightlyPrice.add(peakRate.getAdditionalPrice());
                }

                if (nightlyPrice.compareTo(BigDecimal.ZERO) < 0) {
                    nightlyPrice = BigDecimal.ZERO;
                }

                totalItemPrice = totalItemPrice.add(nightlyPrice);
                currentDate = currentDate.plusDays(1);
            }

            item.setTotalPrice(totalItemPrice);
            totalOrderPrice = totalOrderPrice.add(totalItemPrice);

            orderItemRepository.save(item);
        }

        order.setTotalPrice(totalOrderPrice);
        orderRepository.save(order);
    }
}
