package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {

    @NotBlank
    private String shippingAddress;

    private String couponCode;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public CreateOrderRequest(String shippingAddress, String couponCode) {
        this.shippingAddress = shippingAddress;
        this.couponCode = couponCode;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
