package com.ryanyovanda.airgodabackend.usecase.order;

import com.ryanyovanda.airgodabackend.entity.OrderItem;
import java.util.List;
import java.util.Optional;

public interface OrderItemUsecase {
    OrderItem addOrderItem(OrderItem orderItem); // Tambah item ke order
    Optional<OrderItem> getOrderItemById(Long id); // Ambil item berdasarkan ID
    List<OrderItem> getOrderItemsByOrderId(Long orderId); // Ambil semua item dalam order
    void removeOrderItem(Long id); // Hapus item dari order
}
