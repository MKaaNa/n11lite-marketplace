package com.n11.marketplace.dto.response;

import java.util.List;

public class StoreReviewSummaryResponse {

    private Long storeId;
    private String storeName;
    private double averageRating;
    private int reviewCount;
    private List<StoreReviewItemResponse> reviews;

    public StoreReviewSummaryResponse() {
    }

    public StoreReviewSummaryResponse(
            Long storeId,
            String storeName,
            double averageRating,
            int reviewCount,
            List<StoreReviewItemResponse> reviews) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.reviews = reviews;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public List<StoreReviewItemResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<StoreReviewItemResponse> reviews) {
        this.reviews = reviews;
    }
}
