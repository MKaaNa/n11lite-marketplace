package com.n11.marketplace.payment;

public record CardRegistrationResult(
        String cardUserKey,
        String cardToken,
        String cardAlias,
        String binNumber,
        String lastFourDigits,
        String cardType,
        String cardAssociation) {
}
