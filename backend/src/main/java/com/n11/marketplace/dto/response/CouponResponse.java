package com.n11.marketplace.dto.response;

import java.math.BigDecimal;

public class CouponResponse {

    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal originalTotal;
    private BigDecimal finalTotal;
    private String message;

    public CouponResponse() {
    }

    public CouponResponse(String code, String discountType, BigDecimal discountValue,
            BigDecimal discountAmount, BigDecimal originalTotal, BigDecimal finalTotal, String message) {
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountAmount = discountAmount;
        this.originalTotal = originalTotal;
        this.finalTotal = finalTotal;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getOriginalTotal() {
        return originalTotal;
    }

    public void setOriginalTotal(BigDecimal originalTotal) {
        this.originalTotal = originalTotal;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
