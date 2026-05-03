package com.n11.marketplace.dto.response;

public class SavedCardResponse {

    private String cardToken;
    private String cardAlias;
    private String binNumber;
    private String lastFourDigits;
    private String cardType;
    private String cardAssociation;

    public SavedCardResponse() {
    }

    public SavedCardResponse(
            String cardToken,
            String cardAlias,
            String binNumber,
            String lastFourDigits,
            String cardType,
            String cardAssociation) {
        this.cardToken = cardToken;
        this.cardAlias = cardAlias;
        this.binNumber = binNumber;
        this.lastFourDigits = lastFourDigits;
        this.cardType = cardType;
        this.cardAssociation = cardAssociation;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getCardAlias() {
        return cardAlias;
    }

    public void setCardAlias(String cardAlias) {
        this.cardAlias = cardAlias;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardAssociation() {
        return cardAssociation;
    }

    public void setCardAssociation(String cardAssociation) {
        this.cardAssociation = cardAssociation;
    }
}
