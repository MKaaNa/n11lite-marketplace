package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.CreateOrderRequest;
import com.n11.marketplace.dto.response.OrderItemResponse;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.OrderService;
import java.math.BigDecimal;
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

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    private static final Principal PRINCIPAL = () -> "user@test.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void createOrderShouldReturnOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("Istanbul, Turkey");
        when(orderService.createOrder(eq("user@test.com"), any(CreateOrderRequest.class)))
                .thenReturn(createOrderResponse());

        mockMvc.perform(post("/api/orders")
                        .principal(PRINCIPAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.status").value("PAYMENT_PENDING"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productName").value("Modern Java Guide"));
    }

    @Test
    void createOrderShouldReturnBadRequestForBlankAddress() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest("");

        mockMvc.perform(post("/api/orders")
                        .principal(PRINCIPAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersShouldReturnOrderList() throws Exception {
        when(orderService.getOrders("user@test.com")).thenReturn(List.of(createOrderResponse()));

        mockMvc.perform(get("/api/orders").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50))
                .andExpect(jsonPath("$[0].totalAmount").value(200.00));
    }

    @Test
    void getOrderByIdShouldReturnOrder() throws Exception {
        when(orderService.getOrderById("user@test.com", 50L)).thenReturn(createOrderResponse());

        mockMvc.perform(get("/api/orders/50").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50));
    }

    @Test
    void getOrderByIdShouldReturnNotFound() throws Exception {
        when(orderService.getOrderById("user@test.com", 99L))
                .thenThrow(new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/orders/99").principal(PRINCIPAL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    private OrderResponse createOrderResponse() {
        OrderItemResponse item = new OrderItemResponse(
                100L,
                10L,
                "Modern Java Guide",
                "modern-java-guide",
                new BigDecimal("100.00"),
                2,
                new BigDecimal("200.00"));

        return new OrderResponse(
                50L,
                "PAYMENT_PENDING",
                "PENDING",
                "Istanbul, Turkey",
                new BigDecimal("200.00"),
                LocalDateTime.now(),
                List.of(item));
    }
}
