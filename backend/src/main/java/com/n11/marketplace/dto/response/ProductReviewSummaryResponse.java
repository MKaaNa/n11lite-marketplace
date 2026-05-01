package com.n11.marketplace.dto.response;

import java.util.List;

public class ProductReviewSummaryResponse {

    private double averageRating;
    private int reviewCount;
    private List<ReviewResponse> reviews;

    public ProductReviewSummaryResponse() {
    }

    public ProductReviewSummaryResponse(double averageRating, int reviewCount, List<ReviewResponse> reviews) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.reviews = reviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
}
