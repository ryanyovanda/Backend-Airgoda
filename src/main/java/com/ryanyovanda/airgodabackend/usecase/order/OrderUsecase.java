package com.ryanyovanda.airgodabackend.usecase.order;

import com.ryanyovanda.airgodabackend.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderUsecase {
    Order createOrder(Order order); // Buat order baru
    Optional<Order> getOrderById(Long id); // Ambil order berdasarkan ID
    List<Order> getOrdersByUserId(Long userId); // Ambil semua order milik user
    Order updateOrder(Long id, Order order); // Update order
    void deleteOrder(Long id); // Soft delete order
}
