package com.n11.marketplace.dto.response;

import java.time.LocalDateTime;

public class StoreReviewItemResponse {

    private Long id;
    private String userFullName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String productName;
    private String productSlug;

    public StoreReviewItemResponse() {
    }

    public StoreReviewItemResponse(
            Long id,
            String userFullName,
            Integer rating,
            String comment,
            LocalDateTime createdAt,
            String productName,
            String productSlug) {
        this.id = id;
        this.userFullName = userFullName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.productName = productName;
        this.productSlug = productSlug;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }
}
