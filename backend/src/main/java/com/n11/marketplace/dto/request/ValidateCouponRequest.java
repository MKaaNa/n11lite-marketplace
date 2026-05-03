package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class ValidateCouponRequest {

    @NotBlank
    private String code;

    @NotNull
    @PositiveOrZero
    private BigDecimal cartTotal;

    private String productSlug;

    public ValidateCouponRequest() {
    }

    public ValidateCouponRequest(String code, BigDecimal cartTotal) {
        this.code = code;
        this.cartTotal = cartTotal;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
        this.cartTotal = cartTotal;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }
}
