package com.n11.marketplace.service;

import com.n11.marketplace.dto.response.RecommendedProductResponse;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductView;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.ProductImageRepository;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ProductViewRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final ProductViewRepository productViewRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public RecommendationService(
            ProductViewRepository productViewRepository,
            ProductRepository productRepository,
            ProductImageRepository productImageRepository) {
        this.productViewRepository = productViewRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    public void trackProductView(String sessionId, String slug) {
        Product product = productRepository.findBySlug(slug)
                .filter(Product::isActive)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        productViewRepository.save(new ProductView(sessionId, product));
        log.info("Product view tracked: session={}, slug={}", sessionId, slug);
    }

    @Transactional(readOnly = true)
    public List<RecommendedProductResponse> getRecommendations(String sessionId, int limit) {
        if (sessionId != null && !sessionId.isBlank()) {
            List<ProductView> recentViews = productViewRepository.findRecentViewsWithProductAndCategory(
                    sessionId, PageRequest.of(0, 20));

            if (!recentViews.isEmpty()) {
                Set<Long> viewedProductIds = recentViews.stream()
                        .map(pv -> pv.getProduct().getId())
                        .collect(Collectors.toSet());

                List<Long> categoryIds = recentViews.stream()
                        .map(pv -> pv.getProduct().getCategory().getId())
                        .distinct()
                        .toList();

                List<Product> candidates = productRepository.findByCategoryIdsOrderByViewCountDesc(
                        categoryIds, PageRequest.of(0, limit + viewedProductIds.size()));

                List<Product> recommended = candidates.stream()
                        .filter(p -> !viewedProductIds.contains(p.getId()))
                        .limit(limit)
                        .toList();

                if (!recommended.isEmpty()) {
                    return toRecommendedResponses(recommended);
                }
            }
        }

        List<Product> popular = productRepository.findPopularProducts(PageRequest.of(0, limit));
        return toRecommendedResponses(popular);
    }

    private List<RecommendedProductResponse> toRecommendedResponses(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream().map(Product::getId).toList();
        Map<Long, String> imageByProductId = productImageRepository
                .findByProductIdInOrderByProductIdAscDisplayOrderAsc(productIds)
                .stream()
                .collect(Collectors.toMap(
                        img -> img.getProduct().getId(),
                        img -> img.getImageUrl(),
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new));

        return products.stream()
                .map(p -> new RecommendedProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getSlug(),
                        p.getPrice(),
                        imageByProductId.get(p.getId()),
                        p.getCategory().getName(),
                        p.getStore().getName()))
                .toList();
    }
}
