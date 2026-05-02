package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.UpdateOrderStatusRequest;
import com.n11.marketplace.dto.response.AdminOrderResponse;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.AdminOrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminOrderService adminOrderService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void listAllOrdersReturnsOrders() throws Exception {
        AdminOrderResponse response = buildResponse(1L, "user@test.com", "PAID");
        when(adminOrderService.listAllOrders()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userEmail").value("user@test.com"))
                .andExpect(jsonPath("$[0].status").value("PAID"));
    }

    @Test
    void getOrderByIdReturnsOrder() throws Exception {
        AdminOrderResponse response = buildResponse(1L, "user@test.com", "PAID");
        when(adminOrderService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/admin/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void updateOrderStatusReturnsUpdatedOrder() throws Exception {
        AdminOrderResponse response = buildResponse(1L, "user@test.com", "SHIPPED");
        when(adminOrderService.updateOrderStatus(eq(1L), eq("SHIPPED"))).thenReturn(response);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("SHIPPED");

        mockMvc.perform(put("/api/admin/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void updateOrderStatusWithBlankStatusReturnsBadRequest() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("");

        mockMvc.perform(put("/api/admin/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private AdminOrderResponse buildResponse(Long id, String userEmail, String status) {
        return new AdminOrderResponse(
                id,
                userEmail,
                status,
                "PENDING",
                new BigDecimal("200.00"),
                null,
                BigDecimal.ZERO,
                "Istanbul, Turkey",
                LocalDateTime.now(),
                List.of());
    }
}
