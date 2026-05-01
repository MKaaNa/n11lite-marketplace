package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.dto.response.RecommendedProductResponse;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.RecommendationService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RecommendationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void trackViewShouldReturn400WhenNoSessionId() throws Exception {
        mockMvc.perform(post("/api/recommendations/views/java-guide"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void trackViewShouldSucceedWithSessionId() throws Exception {
        mockMvc.perform(post("/api/recommendations/views/java-guide")
                        .header("X-Session-Id", "session-abc"))
                .andExpect(status().isOk());
    }

    @Test
    void getRecommendationsShouldBePublic() throws Exception {
        List<RecommendedProductResponse> recommendations = List.of(
                new RecommendedProductResponse(1L, "Java Guide", "java-guide", new BigDecimal("100.00"),
                        null, "Books", "Test Store"));

        when(recommendationService.getRecommendations(eq("session-abc"), eq(4)))
                .thenReturn(recommendations);

        mockMvc.perform(get("/api/recommendations")
                        .header("X-Session-Id", "session-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("java-guide"));
    }

    @Test
    void getRecommendationsShouldWorkWithoutSessionId() throws Exception {
        when(recommendationService.getRecommendations(eq(null), eq(4))).thenReturn(List.of());

        mockMvc.perform(get("/api/recommendations"))
                .andExpect(status().isOk());
    }
}
