package com.magazin.repository;

import com.magazin.model.Offer;
import com.magazin.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
