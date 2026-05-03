package com.n11.marketplace.controller;

import com.n11.marketplace.dto.response.StoreReviewSummaryResponse;
import com.n11.marketplace.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Mağaza bilgisi ve mağazanın ürünlerine yapılan yorumların özeti")
public class StoreController {

    private final ReviewService reviewService;

    public StoreController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{storeId}/reviews")
    @Operation(summary = "Mağazanın aktif ürünlerine yapılan tüm yorumlar")
    public ResponseEntity<StoreReviewSummaryResponse> getStoreReviews(@PathVariable Long storeId) {
        return ResponseEntity.ok(reviewService.getReviewsForStore(storeId));
    }
}
