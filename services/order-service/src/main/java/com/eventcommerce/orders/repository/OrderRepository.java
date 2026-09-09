package com.eventcommerce.orders.repository;

import com.eventcommerce.orders.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}