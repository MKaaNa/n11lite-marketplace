package com.n11.marketplace.dto.response;

public class ProductVariantOptionResponse {

    private Long id;
    private String variantType;
    private String variantValue;
    private Integer stock;

    public ProductVariantOptionResponse() {
    }

    public ProductVariantOptionResponse(Long id, String variantType, String variantValue, Integer stock) {
        this.id = id;
        this.variantType = variantType;
        this.variantValue = variantValue;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
