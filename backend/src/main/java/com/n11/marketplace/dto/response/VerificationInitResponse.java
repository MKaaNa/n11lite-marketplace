package com.n11.marketplace.dto.response;

public class VerificationInitResponse {

    private Long verificationId;
    private String message;

    public VerificationInitResponse() {
    }

    public VerificationInitResponse(Long verificationId, String message) {
        this.verificationId = verificationId;
        this.message = message;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
