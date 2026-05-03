package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.request.CreateReviewRequest;
import com.n11.marketplace.dto.response.ProductReviewSummaryResponse;
import com.n11.marketplace.dto.response.ReviewResponse;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.Review;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ReviewRepository;
import com.n11.marketplace.repository.StoreRepository;
import com.n11.marketplace.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, productRepository, userRepository, storeRepository);
    }

    @Test
    void getReviewsForProductReturnsSummary() {
        Product product = createProduct("java-guide");
        User user = createUser("alice@test.com", "Alice");
        Review review = createReview(1L, user, product, 5, "Great book!");

        when(reviewRepository.findByProductSlugOrderByCreatedAtDesc("java-guide"))
                .thenReturn(List.of(review));

        ProductReviewSummaryResponse response = reviewService.getReviewsForProduct("java-guide");

        assertEquals(1, response.getReviewCount());
        assertEquals(5.0, response.getAverageRating());
        assertEquals("Alice", response.getReviews().get(0).getUserFullName());
        assertEquals("Great book!", response.getReviews().get(0).getComment());
    }

    @Test
    void getReviewsForProductReturnsEmptySummaryWhenNoReviews() {
        when(reviewRepository.findByProductSlugOrderByCreatedAtDesc("java-guide"))
                .thenReturn(List.of());

        ProductReviewSummaryResponse response = reviewService.getReviewsForProduct("java-guide");

        assertEquals(0, response.getReviewCount());
        assertEquals(0.0, response.getAverageRating());
        assertEquals(0, response.getReviews().size());
    }

    @Test
    void createReviewSucceeds() {
        Product product = createProduct("java-guide");
        User user = createUser("alice@test.com", "Alice");
        CreateReviewRequest request = new CreateReviewRequest(4, "Solid content");

        when(productRepository.findBySlug("java-guide")).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserEmailAndProductSlug("alice@test.com", "java-guide")).thenReturn(false);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            ReflectionTestUtils.setField(r, "id", 10L);
            return r;
        });

        ReviewResponse response = reviewService.createReview("java-guide", "alice@test.com", request);

        assertEquals(10L, response.getId());
        assertEquals(4, response.getRating());
        assertEquals("Alice", response.getUserFullName());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReviewRejectsDuplicate() {
        Product product = createProduct("java-guide");
        when(productRepository.findBySlug("java-guide")).thenReturn(Optional.of(product));
        when(reviewRepository.existsByUserEmailAndProductSlug("alice@test.com", "java-guide")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewService.createReview("java-guide", "alice@test.com", new CreateReviewRequest(5, "Again")));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void createReviewRejectsInactiveProduct() {
        Product product = createProduct("java-guide");
        product.setActive(false);
        when(productRepository.findBySlug("java-guide")).thenReturn(Optional.of(product));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewService.createReview("java-guide", "alice@test.com", new CreateReviewRequest(5, "Test")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void createReviewRejectsMissingProduct() {
        when(productRepository.findBySlug("missing")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                reviewService.createReview("missing", "alice@test.com", new CreateReviewRequest(3, "Test")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    private Product createProduct(String slug) {
        Category category = new Category("Books", "books");
        Store store = new Store("Test Store");
        Product product = new Product("Java Guide", slug, new BigDecimal("100.00"), 10, category, store);
        ReflectionTestUtils.setField(product, "id", 1L);
        return product;
    }

    private User createUser(String email, String fullName) {
        User user = new User(email, "hash", fullName, null, Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Review createReview(Long id, User user, Product product, int rating, String comment) {
        Review review = new Review(user, product, rating, comment);
        ReflectionTestUtils.setField(review, "id", id);
        return review;
    }
}
