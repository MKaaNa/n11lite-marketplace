package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateOrderStatusRequest {

    @NotBlank
    private String status;

    public UpdateOrderStatusRequest() {
    }

    public UpdateOrderStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
