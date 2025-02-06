package com.ryanyovanda.airgodabackend.usecase.order.impl;

import com.ryanyovanda.airgodabackend.entity.OrderItem;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.OrderItemRepository;
import com.ryanyovanda.airgodabackend.usecase.order.OrderItemUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemUsecaseImpl implements OrderItemUsecase {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemUsecaseImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem addOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public Optional<OrderItem> getOrderItemById(Long id) {
        return orderItemRepository.findById(id);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    public void removeOrderItem(Long id) {
        orderItemRepository.findById(id).ifPresent(orderItemRepository::delete);
    }
}
