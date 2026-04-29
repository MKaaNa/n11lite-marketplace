package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VerifyLoginRequest {

    @NotNull
    private Long verificationId;

    @NotBlank
    @Pattern(regexp = "\\d{6}")
    private String code;

    public VerifyLoginRequest() {
    }

    public VerifyLoginRequest(Long verificationId, String code) {
        this.verificationId = verificationId;
        this.code = code;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(Long verificationId) {
        this.verificationId = verificationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
