package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "store"})
    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"category", "store"})
    Page<Product> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "store"})
    Page<Product> findByActiveTrueAndCategorySlug(String categorySlug, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "store"})
    Page<Product> findByActiveTrueAndNameContainingIgnoreCase(String search, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "store"})
    Page<Product> findByActiveTrueAndCategorySlugAndNameContainingIgnoreCase(
            String categorySlug,
            String search,
            Pageable pageable);
}
