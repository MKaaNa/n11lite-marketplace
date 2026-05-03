package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserEmail(String email);

    Optional<Cart> findByUserId(Long userId);

    @Query("""
            SELECT DISTINCT c FROM Cart c
            JOIN c.user u
            LEFT JOIN FETCH c.items i
            LEFT JOIN FETCH i.product
            WHERE u.email = :email
            """)
    Optional<Cart> findByUserEmailWithItemsAndProducts(@Param("email") String email);
}
