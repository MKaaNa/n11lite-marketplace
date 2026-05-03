package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotNull;

public class ResendLoginVerificationRequest {

    @NotNull
    private Long verificationId;

    public ResendLoginVerificationRequest() {
    }

    public ResendLoginVerificationRequest(Long verificationId) {
        this.verificationId = verificationId;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }
}
