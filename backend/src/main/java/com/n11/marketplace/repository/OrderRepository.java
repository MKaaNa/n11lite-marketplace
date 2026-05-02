package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);

    Optional<Order> findByIdAndUserEmail(Long id, String email);

    List<Order> findAllByOrderByCreatedAtDesc();
}
