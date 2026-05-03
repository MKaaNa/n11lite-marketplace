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
        return getRecommendations(sessionId, limit, null);
    }

    @Transactional(readOnly = true)
    public List<RecommendedProductResponse> getRecommendations(String sessionId, int limit, String currentSlug) {
        Map<Long, Product> recommendedById = new LinkedHashMap<>();
        Long currentProductId = null;

        if (currentSlug != null && !currentSlug.isBlank()) {
            Product currentProduct = productRepository.findBySlug(currentSlug)
                    .filter(Product::isActive)
                    .orElse(null);
            if (currentProduct != null && currentProduct.getCategory() != null) {
                currentProductId = currentProduct.getId();
                final Long excludedProductId = currentProductId;
                List<Product> sameCategoryCandidates = productRepository.findByCategoryIdsOrderByViewCountDesc(
                        List.of(currentProduct.getCategory().getId()),
                        PageRequest.of(0, Math.max(limit * 3, 12)));
                appendUniqueProducts(
                        recommendedById,
                        sameCategoryCandidates.stream()
                                .filter(candidate -> !candidate.getId().equals(excludedProductId))
                                .toList(),
                        limit);
            }
        }

        if (recommendedById.size() < limit && sessionId != null && !sessionId.isBlank()) {
            List<ProductView> recentViews = productViewRepository.findRecentViewsWithProductAndCategory(
                    sessionId, PageRequest.of(0, 20));

            if (!recentViews.isEmpty()) {
                Set<Long> viewedProductIds = recentViews.stream()
                        .map(pv -> pv.getProduct().getId())
                        .collect(Collectors.toSet());

                List<Long> categoryIds = recentViews.stream()
                        .map(ProductView::getProduct)
                        .filter(product -> product.getCategory() != null)
                        .map(product -> product.getCategory().getId())
                        .distinct()
                        .toList();

                if (!categoryIds.isEmpty()) {
                    List<Product> candidates = productRepository.findByCategoryIdsOrderByViewCountDesc(
                            categoryIds, PageRequest.of(0, limit + viewedProductIds.size()));

                    List<Product> sessionBased = candidates.stream()
                            .filter(p -> !viewedProductIds.contains(p.getId()))
                            .toList();

                    appendUniqueProducts(recommendedById, sessionBased, limit);
                }
            }
        }

        if (recommendedById.size() < limit) {
            List<Product> popular = productRepository.findPopularProducts(PageRequest.of(0, limit * 3));
            if (currentProductId != null) {
                final Long excludedProductId = currentProductId;
                appendUniqueProducts(
                        recommendedById,
                        popular.stream()
                                .filter(product -> !product.getId().equals(excludedProductId))
                                .toList(),
                        limit);
            } else {
                appendUniqueProducts(recommendedById, popular, limit);
            }
        }

        return toRecommendedResponses(recommendedById.values().stream().limit(limit).toList());
    }

    private void appendUniqueProducts(Map<Long, Product> target, List<Product> source, int limit) {
        for (Product product : source) {
            if (target.size() >= limit) {
                return;
            }
            target.putIfAbsent(product.getId(), product);
        }
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
