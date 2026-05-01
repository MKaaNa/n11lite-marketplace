package com.n11.marketplace.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.config.SecurityConfig;
import com.n11.marketplace.dto.response.ProductReviewSummaryResponse;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.security.JwtUtil;
import com.n11.marketplace.security.UserDetailsServiceImpl;
import com.n11.marketplace.service.ReviewService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class ReviewSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void getReviewsShouldBePublic() throws Exception {
        when(reviewService.getReviewsForProduct("java-guide"))
                .thenReturn(new ProductReviewSummaryResponse(0.0, 0, List.of()));

        mockMvc.perform(get("/api/products/java-guide/reviews"))
                .andExpect(status().isOk());
    }

    @Test
    void createReviewShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/products/java-guide/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":5,\"comment\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }
}
