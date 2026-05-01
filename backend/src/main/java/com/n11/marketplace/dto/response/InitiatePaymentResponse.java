package com.n11.marketplace.dto.response;

public class InitiatePaymentResponse {

    private Long orderId;
    private Long paymentId;
    private String token;
    private String paymentPageUrl;
    private String status;

    public InitiatePaymentResponse() {
    }

    public InitiatePaymentResponse(Long orderId, Long paymentId, String token, String paymentPageUrl, String status) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.token = token;
        this.paymentPageUrl = paymentPageUrl;
        this.status = status;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPaymentPageUrl() {
        return paymentPageUrl;
    }

    public void setPaymentPageUrl(String paymentPageUrl) {
        this.paymentPageUrl = paymentPageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
