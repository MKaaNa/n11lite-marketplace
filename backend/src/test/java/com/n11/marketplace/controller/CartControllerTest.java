package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.marketplace.dto.request.AddToCartRequest;
import com.n11.marketplace.dto.request.UpdateCartItemRequest;
import com.n11.marketplace.dto.response.CartItemResponse;
import com.n11.marketplace.dto.response.CartResponse;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.CartService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CartControllerTest {

    private static final Principal PRINCIPAL = () -> "user@test.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void getCartShouldReturnCart() throws Exception {
        when(cartService.getCart("user@test.com")).thenReturn(createCartResponse());

        mockMvc.perform(get("/api/cart").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Modern Java Guide"))
                .andExpect(jsonPath("$.totalAmount").value(200.00));
    }

    @Test
    void addItemShouldReturnCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest(10L, 2);
        when(cartService.addItem(eq("user@test.com"), org.mockito.ArgumentMatchers.any(AddToCartRequest.class)))
                .thenReturn(createCartResponse());

        mockMvc.perform(post("/api/cart/items")
                        .principal(PRINCIPAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void updateItemShouldReturnCart() throws Exception {
        UpdateCartItemRequest request = new UpdateCartItemRequest(2);
        when(cartService.updateItem(eq("user@test.com"), eq(100L),
                org.mockito.ArgumentMatchers.any(UpdateCartItemRequest.class)))
                .thenReturn(createCartResponse());

        mockMvc.perform(put("/api/cart/items/100")
                        .principal(PRINCIPAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(100));
    }

    @Test
    void removeItemShouldReturnCart() throws Exception {
        when(cartService.removeItem("user@test.com", 100L)).thenReturn(new CartResponse(1L, List.of(), BigDecimal.ZERO));

        mockMvc.perform(delete("/api/cart/items/100").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void clearCartShouldReturnCart() throws Exception {
        when(cartService.clearCart("user@test.com")).thenReturn(new CartResponse(1L, List.of(), BigDecimal.ZERO));

        mockMvc.perform(delete("/api/cart/items").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void addItemShouldReturnBadRequestForInvalidQuantity() throws Exception {
        AddToCartRequest request = new AddToCartRequest(10L, 0);

        mockMvc.perform(post("/api/cart/items")
                        .principal(PRINCIPAL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private CartResponse createCartResponse() {
        CartItemResponse item = new CartItemResponse(
                100L,
                10L,
                "Modern Java Guide",
                "modern-java-guide",
                "https://example.com/java.jpg",
                new BigDecimal("100.00"),
                2,
                new BigDecimal("200.00"));

        return new CartResponse(1L, List.of(item), new BigDecimal("200.00"));
    }
}
