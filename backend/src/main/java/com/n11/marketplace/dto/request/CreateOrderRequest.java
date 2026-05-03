package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.AssertTrue;

public class CreateOrderRequest {

    private String shippingAddress;
    private Long savedAddressId;
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

    @AssertTrue(message = "Teslimat adresi veya kayıtlı adres seçilmeli")
    public boolean isAddressProvided() {
        if (savedAddressId != null) {
            return true;
        }
        return shippingAddress != null && !shippingAddress.isBlank();
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getSavedAddressId() {
        return savedAddressId;
    }

    public void setSavedAddressId(Long savedAddressId) {
        this.savedAddressId = savedAddressId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
