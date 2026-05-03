package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.RegisterPaymentCardRequest;
import com.n11.marketplace.dto.response.SavedCardResponse;
import com.n11.marketplace.service.PaymentCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me/payment-cards")
@Tag(name = "My payment cards (Iyzico)")
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @GetMapping
    @Operation(summary = "List cards saved at Iyzico for this user")
    public ResponseEntity<List<SavedCardResponse>> list(Principal principal) {
        return ResponseEntity.ok(paymentCardService.list(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Register a card with Iyzico (sandbox test cards only in demo)")
    public ResponseEntity<SavedCardResponse> register(
            Principal principal, @Valid @RequestBody RegisterPaymentCardRequest request) {
        return ResponseEntity.ok(paymentCardService.register(principal.getName(), request));
    }

    @DeleteMapping("/{cardToken:.+}")
    @Operation(summary = "Remove a saved card from Iyzico")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable String cardToken) {
        paymentCardService.delete(principal.getName(), cardToken);
        return ResponseEntity.noContent().build();
    }
}
