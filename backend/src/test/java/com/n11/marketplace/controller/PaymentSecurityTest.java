package com.n11.marketplace.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.config.SecurityConfig;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.security.JwtUtil;
import com.n11.marketplace.security.UserDetailsServiceImpl;
import com.n11.marketplace.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class PaymentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void iyzicoCallbackShouldBePublicWithoutToken() throws Exception {
        when(paymentService.handleCallback("checkout-token")).thenReturn(
                new PaymentCallbackResponse(50L, 80L, "SUCCESS", "PAID", "Payment successful"));

        mockMvc.perform(post("/api/payments/iyzico/callback").param("token", "checkout-token"))
                .andExpect(status().isOk());
    }

    @Test
    void iyzicoCallbackWithoutTokenShouldNotRequireAuthentication() throws Exception {
        when(paymentService.handleCallback(isNull()))
                .thenThrow(new BusinessException("Payment token is required", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/payments/iyzico/callback"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/payments/orders/50/checkout"))
                .andExpect(status().isUnauthorized());
    }
}
