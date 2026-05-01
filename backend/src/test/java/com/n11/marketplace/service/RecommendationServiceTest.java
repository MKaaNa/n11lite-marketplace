package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.response.RecommendedProductResponse;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductView;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.ProductImageRepository;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ProductViewRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private ProductViewRepository productViewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(
                productViewRepository, productRepository, productImageRepository);
    }

    @Test
    void trackProductViewSucceeds() {
        Product product = createProduct(1L, "java-guide", "Books");
        when(productRepository.findBySlug("java-guide")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        recommendationService.trackProductView("session-abc", "java-guide");

        verify(productViewRepository).save(any(ProductView.class));
        verify(productRepository).save(product);
    }

    @Test
    void trackProductViewRejectsUnknownProduct() {
        when(productRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                recommendationService.trackProductView("session-abc", "unknown"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void getRecommendationsReturnsSameCategoryProducts() {
        Category booksCategory = createCategory(10L, "Books");
        Product viewed = createProductWithCategory(1L, "java-guide", booksCategory);
        Product recommended = createProductWithCategory(2L, "clean-code", booksCategory);

        ProductView view = new ProductView("session-abc", viewed);

        when(productViewRepository.findRecentViewsWithProductAndCategory(eq("session-abc"), any(Pageable.class)))
                .thenReturn(List.of(view));
        when(productRepository.findByCategoryIdsOrderByViewCountDesc(any(), any(Pageable.class)))
                .thenReturn(List.of(recommended));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(any()))
                .thenReturn(List.of());

        List<RecommendedProductResponse> result = recommendationService.getRecommendations("session-abc", 4);

        assertFalse(result.isEmpty());
        assertEquals("clean-code", result.get(0).getSlug());
    }

    @Test
    void getRecommendationsExcludesAlreadyViewedProducts() {
        Category booksCategory = createCategory(10L, "Books");
        Product viewed = createProductWithCategory(1L, "java-guide", booksCategory);

        ProductView view = new ProductView("session-abc", viewed);

        when(productViewRepository.findRecentViewsWithProductAndCategory(eq("session-abc"), any(Pageable.class)))
                .thenReturn(List.of(view));
        when(productRepository.findByCategoryIdsOrderByViewCountDesc(any(), any(Pageable.class)))
                .thenReturn(List.of(viewed));
        when(productRepository.findPopularProducts(any(Pageable.class))).thenReturn(List.of());

        List<RecommendedProductResponse> result = recommendationService.getRecommendations("session-abc", 4);

        assertEquals(0, result.size());
    }

    @Test
    void getRecommendationsFallsBackToPopularWhenNoSessionViews() {
        Product popular = createProduct(5L, "popular-product", "Electronics");

        when(productViewRepository.findRecentViewsWithProductAndCategory(eq("session-new"), any(Pageable.class)))
                .thenReturn(List.of());
        when(productRepository.findPopularProducts(any(Pageable.class))).thenReturn(List.of(popular));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(any()))
                .thenReturn(List.of());

        List<RecommendedProductResponse> result = recommendationService.getRecommendations("session-new", 4);

        assertEquals(1, result.size());
        assertEquals("popular-product", result.get(0).getSlug());
    }

    @Test
    void getRecommendationsFallsBackToPopularWhenNoSessionId() {
        Product popular = createProduct(5L, "popular-product", "Electronics");

        when(productRepository.findPopularProducts(any(Pageable.class))).thenReturn(List.of(popular));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(any()))
                .thenReturn(List.of());

        List<RecommendedProductResponse> result = recommendationService.getRecommendations(null, 4);

        assertEquals(1, result.size());
    }

    private Product createProduct(Long id, String slug, String categoryName) {
        Category category = createCategory(id * 10, categoryName);
        return createProductWithCategory(id, slug, category);
    }

    private Product createProductWithCategory(Long id, String slug, Category category) {
        Store store = new Store("Test Store");
        ReflectionTestUtils.setField(store, "id", id * 100);
        Product product = new Product(slug, slug, new BigDecimal("100.00"), 10, category, store);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category(name, name.toLowerCase());
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }
}
