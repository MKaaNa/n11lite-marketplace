package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserEmail(String email);

    Optional<Cart> findByUserId(Long userId);
}
