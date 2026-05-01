package com.n11.marketplace.repository;

import com.n11.marketplace.entity.ProductView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {

    @Query("SELECT pv FROM ProductView pv JOIN FETCH pv.product p JOIN FETCH p.category WHERE pv.sessionId = :sessionId ORDER BY pv.createdAt DESC")
    List<ProductView> findRecentViewsWithProductAndCategory(
            @Param("sessionId") String sessionId,
            org.springframework.data.domain.Pageable pageable);
}
