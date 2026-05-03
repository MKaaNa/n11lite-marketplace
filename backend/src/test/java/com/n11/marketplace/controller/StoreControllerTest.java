package com.n11.marketplace.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.dto.response.StoreReviewItemResponse;
import com.n11.marketplace.dto.response.StoreReviewSummaryResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StoreController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void getStoreReviewsShouldReturnSummary() throws Exception {
        StoreReviewSummaryResponse summary = new StoreReviewSummaryResponse(
                1L,
                "BookNest",
                4.5,
                1,
                List.of(new StoreReviewItemResponse(
                        10L,
                        "Alice",
                        5,
                        "Great book",
                        LocalDateTime.now(),
                        "Modern Java Guide",
                        "modern-java-guide")));

        when(reviewService.getReviewsForStore(1L)).thenReturn(summary);

        mockMvc.perform(get("/api/stores/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storeId").value(1))
                .andExpect(jsonPath("$.storeName").value("BookNest"))
                .andExpect(jsonPath("$.reviewCount").value(1))
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.reviews[0].productSlug").value("modern-java-guide"));
    }

    @Test
    void getStoreReviewsShouldReturnNotFoundWhenMissing() throws Exception {
        when(reviewService.getReviewsForStore(99L))
                .thenThrow(new BusinessException("Store not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/stores/99/reviews"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Store not found"));
    }
}
