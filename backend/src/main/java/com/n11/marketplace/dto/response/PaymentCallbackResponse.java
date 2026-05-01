package com.n11.marketplace.dto.response;

public class PaymentCallbackResponse {

    private Long orderId;
    private Long paymentId;
    private String status;
    private String orderStatus;
    private String message;

    public PaymentCallbackResponse() {
    }

    public PaymentCallbackResponse(Long orderId, Long paymentId, String status, String orderStatus, String message) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.status = status;
        this.orderStatus = orderStatus;
        this.message = message;
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
