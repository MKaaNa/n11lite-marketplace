package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.CreateReviewRequest;
import com.n11.marketplace.dto.response.ProductReviewSummaryResponse;
import com.n11.marketplace.dto.response.ReviewResponse;
import com.n11.marketplace.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{slug}/reviews")
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @Operation(summary = "Get reviews for a product")
    public ResponseEntity<ProductReviewSummaryResponse> getReviews(@PathVariable String slug) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(slug));
    }

    @PostMapping
    @Operation(summary = "Create a review for a product")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable String slug,
            Principal principal,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(slug, principal.getName(), request));
    }
}
