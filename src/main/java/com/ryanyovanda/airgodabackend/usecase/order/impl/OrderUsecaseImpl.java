package com.ryanyovanda.airgodabackend.usecase.order.impl;

import com.ryanyovanda.airgodabackend.entity.Order;
import com.ryanyovanda.airgodabackend.infrastructure.order.repository.OrderRepository;
import com.ryanyovanda.airgodabackend.usecase.order.OrderUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderUsecaseImpl implements OrderUsecase {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderUsecaseImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrder(Long id, Order order) {
        return orderRepository.findById(id).map(existingOrder -> {
            existingOrder.setTotalPrice(order.getTotalPrice());
            existingOrder.setIsPaid(order.getIsPaid());
            return orderRepository.save(existingOrder);
        }).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            orderRepository.delete(order);
        });
    }
}
