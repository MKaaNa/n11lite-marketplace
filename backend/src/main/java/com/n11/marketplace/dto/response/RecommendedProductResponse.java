package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class RecommendedProductResponse {

    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private String imageUrl;
    private String categoryName;
    private String storeName;

    public RecommendedProductResponse() {
    }

    public RecommendedProductResponse(
            Long id,
            String name,
            String slug,
            BigDecimal price,
            String imageUrl,
            String categoryName,
            String storeName) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.storeName = storeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
