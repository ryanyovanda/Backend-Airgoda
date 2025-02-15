package com.ryanyovanda.airgodabackend.infrastructure.order.repository;

import com.ryanyovanda.airgodabackend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
