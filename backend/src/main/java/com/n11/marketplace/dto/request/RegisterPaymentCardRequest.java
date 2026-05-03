package com.n11.marketplace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterPaymentCardRequest {

    @NotBlank
    @Size(max = 64)
    private String cardAlias;

    @NotBlank
    @Size(max = 120)
    private String cardHolderName;

    @NotBlank
    @Pattern(regexp = "\\d{12,19}")
    private String cardNumber;

    @NotBlank
    @Pattern(regexp = "\\d{2}")
    private String expireMonth;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    private String expireYear;

    public String getCardAlias() {
        return cardAlias;
    }

    public void setCardAlias(String cardAlias) {
        this.cardAlias = cardAlias;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireMonth() {
        return expireMonth;
    }

    public void setExpireMonth(String expireMonth) {
        this.expireMonth = expireMonth;
    }

    public String getExpireYear() {
        return expireYear;
    }

    public void setExpireYear(String expireYear) {
        this.expireYear = expireYear;
    }
}
