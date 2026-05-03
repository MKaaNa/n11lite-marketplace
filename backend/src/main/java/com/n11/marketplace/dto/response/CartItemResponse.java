package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String imageUrl;
    private Long productVariantId;
    private String variantType;
    private String variantValue;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;

    public CartItemResponse() {
    }

    public CartItemResponse(
            Long id,
            Long productId,
            String productName,
            String productSlug,
            String imageUrl,
            Long productVariantId,
            String variantType,
            String variantValue,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productSlug = productSlug;
        this.imageUrl = imageUrl;
        this.productVariantId = productVariantId;
        this.variantType = variantType;
        this.variantValue = variantValue;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getProductVariantId() {
        return productVariantId;
    }

    public void setProductVariantId(Long productVariantId) {
        this.productVariantId = productVariantId;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getVariantValue() {
        return variantValue;
    }

    public void setVariantValue(String variantValue) {
        this.variantValue = variantValue;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
