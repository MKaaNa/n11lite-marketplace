package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class ProductSummaryResponse {

    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private Integer stock;
    private String badge;
    private String mainImageUrl;
    private CategoryResponse category;
    private StoreResponse store;

    public ProductSummaryResponse() {
    }

    public ProductSummaryResponse(
            Long id,
            String name,
            String slug,
            BigDecimal price,
            Integer stock,
            String badge,
            String mainImageUrl,
            CategoryResponse category,
            StoreResponse store) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.price = price;
        this.stock = stock;
        this.badge = badge;
        this.mainImageUrl = mainImageUrl;
        this.category = category;
        this.store = store;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public StoreResponse getStore() {
        return store;
    }

    public void setStore(StoreResponse store) {
        this.store = store;
    }
}
