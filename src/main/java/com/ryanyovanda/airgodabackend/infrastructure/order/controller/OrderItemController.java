package com.ryanyovanda.airgodabackend.infrastructure.order.controller;

import com.ryanyovanda.airgodabackend.entity.OrderItem;
import com.ryanyovanda.airgodabackend.usecase.order.OrderItemUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemUsecase orderItemUsecase;

    @Autowired
    public OrderItemController(OrderItemUsecase orderItemUsecase) {
        this.orderItemUsecase = orderItemUsecase;
    }

    @PostMapping
    public ResponseEntity<OrderItem> addOrderItem(@RequestBody OrderItem orderItem) {
        OrderItem savedOrderItem = orderItemUsecase.addOrderItem(orderItem);
        return ResponseEntity.ok(savedOrderItem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        Optional<OrderItem> orderItem = orderItemUsecase.getOrderItemById(id);
        return orderItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItem> orderItems = orderItemUsecase.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItems);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeOrderItem(@PathVariable Long id) {
        orderItemUsecase.removeOrderItem(id);
        return ResponseEntity.noContent().build();
    }
}
