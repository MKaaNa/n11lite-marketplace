package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.CreateReviewRequest;
import com.n11.marketplace.dto.response.ProductReviewSummaryResponse;
import com.n11.marketplace.dto.response.ReviewResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.ReviewService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ReviewControllerTest {

    private static final Principal PRINCIPAL = () -> "user@test.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void getReviewsShouldReturnSummary() throws Exception {
        ProductReviewSummaryResponse summary = new ProductReviewSummaryResponse(4.5, 2, List.of(
                new ReviewResponse(1L, "Alice", 5, "Great!", LocalDateTime.now()),
                new ReviewResponse(2L, "Bob", 4, "Good.", LocalDateTime.now())));

        when(reviewService.getReviewsForProduct("java-guide")).thenReturn(summary);

        mockMvc.perform(get("/api/products/java-guide/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewCount").value(2))
                .andExpect(jsonPath("$.averageRating").value(4.5));
    }

    @Test
    void createReviewShouldSucceedWithPrincipal() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(5, "Excellent!");
        ReviewResponse response = new ReviewResponse(1L, "Alice", 5, "Excellent!", LocalDateTime.now());

        when(reviewService.createReview(eq("java-guide"), eq("user@test.com"), any(CreateReviewRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/products/java-guide/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void createReviewShouldReturn400WhenInvalidRating() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(10, "Invalid rating");

        mockMvc.perform(post("/api/products/java-guide/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(PRINCIPAL))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReviewShouldReturn400ForDuplicate() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(4, "Second review");

        when(reviewService.createReview(eq("java-guide"), eq("user@test.com"), any(CreateReviewRequest.class)))
                .thenThrow(new BusinessException("You have already reviewed this product", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/products/java-guide/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(PRINCIPAL))
                .andExpect(status().isBadRequest());
    }
}
