package com.n11.marketplace.controller;

import com.n11.marketplace.dto.response.MessageResponse;
import com.n11.marketplace.dto.response.RecommendedProductResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/views/{slug}")
    @Operation(summary = "Track a product view for session-based recommendations")
    public ResponseEntity<MessageResponse> trackView(
            @PathVariable String slug,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BusinessException("X-Session-Id header is required", HttpStatus.BAD_REQUEST);
        }

        recommendationService.trackProductView(sessionId, slug);
        return ResponseEntity.ok(new MessageResponse("View tracked"));
    }

    @GetMapping
    @Operation(summary = "Get recommended products for current session")
    public ResponseEntity<List<RecommendedProductResponse>> getRecommendations(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @RequestParam(defaultValue = "4") int limit,
            @RequestParam(required = false) String currentSlug) {
        if (currentSlug == null || currentSlug.isBlank()) {
            return ResponseEntity.ok(recommendationService.getRecommendations(sessionId, limit));
        }
        return ResponseEntity.ok(recommendationService.getRecommendations(sessionId, limit, currentSlug));
    }
}
