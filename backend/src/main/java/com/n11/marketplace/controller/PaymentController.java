package com.n11.marketplace.controller;

import com.n11.marketplace.dto.response.InitiatePaymentResponse;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.dto.response.PaymentResponse;
import com.n11.marketplace.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/checkout")
    @Operation(summary = "Start Iyzico checkout")
    public ResponseEntity<InitiatePaymentResponse> initiateCheckout(
            Principal principal,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.initiateCheckout(principal.getName(), orderId));
    }

    @PostMapping("/iyzico/callback")
    @Operation(summary = "Handle Iyzico callback")
    public ResponseEntity<PaymentCallbackResponse> handleIyzicoCallback(
            @RequestParam(required = false) String token) {
        return ResponseEntity.ok(paymentService.handleCallback(token));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get payment status for order")
    public ResponseEntity<PaymentResponse> getPaymentForOrder(Principal principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentForOrder(principal.getName(), orderId));
    }
}
