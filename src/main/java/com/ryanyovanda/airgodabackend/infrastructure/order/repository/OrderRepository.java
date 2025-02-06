package com.ryanyovanda.airgodabackend.infrastructure.order.repository;

import com.ryanyovanda.airgodabackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>  {
    List<Order> findByUserId(Long userId);
    List<Order> findByUserIdAndIsPaidFalse(Long userId);

}
