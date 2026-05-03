package com.n11.marketplace.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.config.SecurityConfig;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.security.JwtUtil;
import com.n11.marketplace.security.UserDetailsServiceImpl;
import com.n11.marketplace.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponController.class)
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class CouponSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void validateCouponShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/coupons/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"N11WELCOME\",\"cartTotal\":500}"))
                .andExpect(status().isUnauthorized());
    }
}
