package com.n11.marketplace.service;

import com.n11.marketplace.dto.request.CreateReviewRequest;
import com.n11.marketplace.dto.response.ProductReviewSummaryResponse;
import com.n11.marketplace.dto.response.ReviewResponse;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.Review;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ReviewRepository;
import com.n11.marketplace.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProductReviewSummaryResponse getReviewsForProduct(String slug) {
        List<Review> reviews = reviewRepository.findByProductSlugOrderByCreatedAtDesc(slug);

        double averageRating = reviews.isEmpty()
                ? 0.0
                : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getUser().getFullName(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt()))
                .toList();

        return new ProductReviewSummaryResponse(
                Math.round(averageRating * 10.0) / 10.0,
                reviews.size(),
                reviewResponses);
    }

    @Transactional
    public ReviewResponse createReview(String slug, String userEmail, CreateReviewRequest request) {
        Product product = productRepository.findBySlug(slug)
                .filter(Product::isActive)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        if (reviewRepository.existsByUserEmailAndProductSlug(userEmail, slug)) {
            throw new BusinessException("You have already reviewed this product", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));

        Review review = new Review(user, product, request.getRating(), request.getComment());
        Review saved = reviewRepository.save(review);

        log.info("Review created for product {} by user {}", slug, userEmail);
        return new ReviewResponse(
                saved.getId(),
                user.getFullName(),
                saved.getRating(),
                saved.getComment(),
                saved.getCreatedAt());
    }
}
