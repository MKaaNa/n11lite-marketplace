package com.n11.marketplace.repository;

import com.n11.marketplace.entity.ProductVariant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByIdAndActiveTrue(Long id);

    List<ProductVariant> findByProductIdAndActiveTrueOrderByVariantValueAsc(Long productId);
}
