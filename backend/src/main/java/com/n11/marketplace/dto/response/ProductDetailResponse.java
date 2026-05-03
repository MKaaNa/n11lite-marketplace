package com.n11.marketplace.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ProductDetailResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Long soldCount;
    private Long viewCount;
    private String badge;
    private CategoryResponse category;
    private StoreResponse store;
    private List<ProductImageResponse> images;
    private List<ProductVariantOptionResponse> variants;
    private String productCouponCode;
    private String productCouponLabel;

    public ProductDetailResponse() {
    }

    public ProductDetailResponse(
            Long id,
            String name,
            String slug,
            String description,
            BigDecimal price,
            Integer stock,
            Long soldCount,
            Long viewCount,
            String badge,
            CategoryResponse category,
            StoreResponse store,
            List<ProductImageResponse> images,
            List<ProductVariantOptionResponse> variants) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.soldCount = soldCount;
        this.viewCount = viewCount;
        this.badge = badge;
        this.category = category;
        this.store = store;
        this.images = images;
        this.variants = variants;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Long soldCount) {
        this.soldCount = soldCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
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

    public List<ProductImageResponse> getImages() {
        return images;
    }

    public void setImages(List<ProductImageResponse> images) {
        this.images = images;
    }

    public List<ProductVariantOptionResponse> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantOptionResponse> variants) {
        this.variants = variants;
    }

    public String getProductCouponCode() {
        return productCouponCode;
    }

    public void setProductCouponCode(String productCouponCode) {
        this.productCouponCode = productCouponCode;
    }

    public String getProductCouponLabel() {
        return productCouponLabel;
    }

    public void setProductCouponLabel(String productCouponLabel) {
        this.productCouponLabel = productCouponLabel;
    }
}
