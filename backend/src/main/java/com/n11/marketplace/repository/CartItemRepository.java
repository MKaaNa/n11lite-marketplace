package com.n11.marketplace.repository;

import com.n11.marketplace.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("""
            SELECT ci
            FROM CartItem ci
            WHERE ci.cart.id = :cartId
              AND ci.product.id = :productId
              AND (
                    (:productVariantId IS NULL AND ci.productVariant IS NULL)
                    OR ci.productVariant.id = :productVariantId
                  )
            """)
    Optional<CartItem> findByCartIdAndProductIdAndVariant(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId,
            @Param("productVariantId") Long productVariantId);
}
