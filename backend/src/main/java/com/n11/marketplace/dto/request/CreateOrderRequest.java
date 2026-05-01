package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {

    @NotBlank
    private String shippingAddress;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
