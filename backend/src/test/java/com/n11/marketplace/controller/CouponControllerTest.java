package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.ValidateCouponRequest;
import com.n11.marketplace.dto.response.CouponResponse;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.CouponService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void validateCouponSuccess() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("N11WELCOME", new BigDecimal("500.00"));
        when(couponService.validateCoupon(eq("N11WELCOME"), any(BigDecimal.class))).thenReturn(
                new CouponResponse(
                        "N11WELCOME",
                        "PERCENT",
                        new BigDecimal("10.00"),
                        new BigDecimal("50.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("450.00"),
                        null,
                        "Coupon applied successfully"));

        mockMvc.perform(post("/api/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("N11WELCOME"))
                .andExpect(jsonPath("$.discountAmount").value(50.00))
                .andExpect(jsonPath("$.finalTotal").value(450.00));
    }

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        ValidateCouponRequest request = new ValidateCouponRequest("", new BigDecimal("100.00"));

        mockMvc.perform(post("/api/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
