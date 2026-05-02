package com.n11.marketplace.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderResponse {

    private Long id;
    private String userEmail;
    private String status;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private String couponCode;
    private BigDecimal discountAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public AdminOrderResponse() {
    }

    public AdminOrderResponse(Long id, String userEmail, String status, String paymentStatus,
            BigDecimal totalAmount, String couponCode, BigDecimal discountAmount,
            String shippingAddress, LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.userEmail = userEmail;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
