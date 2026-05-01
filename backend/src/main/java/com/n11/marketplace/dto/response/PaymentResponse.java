package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class PaymentResponse {

    private Long orderId;
    private Long paymentId;
    private String status;
    private String paymentPageUrl;
    private BigDecimal price;

    public PaymentResponse() {
    }

    public PaymentResponse(Long orderId, Long paymentId, String status, String paymentPageUrl, BigDecimal price) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.status = status;
        this.paymentPageUrl = paymentPageUrl;
        this.price = price;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentPageUrl() {
        return paymentPageUrl;
    }

    public void setPaymentPageUrl(String paymentPageUrl) {
        this.paymentPageUrl = paymentPageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
