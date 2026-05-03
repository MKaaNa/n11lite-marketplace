package com.n11.marketplace.repository;

import com.n11.marketplace.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.product.slug = :slug ORDER BY r.createdAt DESC")
    List<Review> findByProductSlugOrderByCreatedAtDesc(@Param("slug") String slug);

    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.user.email = :userEmail AND r.product.slug = :productSlug")
    boolean existsByUserEmailAndProductSlug(
            @Param("userEmail") String userEmail,
            @Param("productSlug") String productSlug);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.product p JOIN p.store s WHERE s.id = :storeId AND p.active = true ORDER BY r.createdAt DESC")
    List<Review> findByStoreIdWithDetailsOrderByCreatedAtDesc(@Param("storeId") Long storeId);
}
