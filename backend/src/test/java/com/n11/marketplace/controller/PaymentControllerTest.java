package com.n11.marketplace.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.isNull;

import com.n11.marketplace.dto.response.InitiatePaymentResponse;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.dto.response.PaymentResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.PaymentService;
import java.math.BigDecimal;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PaymentControllerTest {

    private static final Principal PRINCIPAL = () -> "user@test.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void initiateCheckoutShouldReturnPaymentPage() throws Exception {
        when(paymentService.initiateCheckout("user@test.com", 50L))
                .thenReturn(new InitiatePaymentResponse(
                        50L,
                        80L,
                        "checkout-token",
                        "https://sandbox-checkout.iyzico.com/pay",
                        "PENDING"));

        mockMvc.perform(post("/api/payments/orders/50/checkout").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(50))
                .andExpect(jsonPath("$.paymentId").value(80))
                .andExpect(jsonPath("$.token").value("checkout-token"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void initiateCheckoutShouldReturnNotFoundForAnotherUsersOrder() throws Exception {
        when(paymentService.initiateCheckout("user@test.com", 99L))
                .thenThrow(new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/payments/orders/99/checkout").principal(PRINCIPAL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void callbackShouldReturnPaymentResult() throws Exception {
        when(paymentService.handleCallback("checkout-token"))
                .thenReturn(new PaymentCallbackResponse(
                        50L,
                        80L,
                        "SUCCESS",
                        "PAID",
                        "Payment successful"));

        mockMvc.perform(post("/api/payments/iyzico/callback").param("token", "checkout-token"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ödeme başarılı")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sipariş No")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("#50")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ödendi")));
    }

    @Test
    void callbackShouldReturnNotFoundForUnknownToken() throws Exception {
        when(paymentService.handleCallback("missing-token"))
                .thenThrow(new BusinessException("Payment not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/api/payments/iyzico/callback").param("token", "missing-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Payment not found"));
    }

    @Test
    void callbackShouldReturnBadRequestWhenTokenIsMissing() throws Exception {
        when(paymentService.handleCallback(isNull()))
                .thenThrow(new BusinessException("Payment token is required", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/payments/iyzico/callback"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Payment token is required"));
    }

    @Test
    void getPaymentForOrderShouldReturnPayment() throws Exception {
        when(paymentService.getPaymentForOrder("user@test.com", 50L))
                .thenReturn(new PaymentResponse(
                        50L,
                        80L,
                        "PENDING",
                        "https://sandbox-checkout.iyzico.com/pay",
                        new BigDecimal("200.00")));

        mockMvc.perform(get("/api/payments/orders/50").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(50))
                .andExpect(jsonPath("$.paymentId").value(80))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
