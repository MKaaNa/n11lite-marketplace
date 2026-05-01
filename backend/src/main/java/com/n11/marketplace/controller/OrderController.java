package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.CreateOrderRequest;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create order from cart")
    public ResponseEntity<OrderResponse> createOrder(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(principal.getName(), request));
    }

    @GetMapping
    @Operation(summary = "List current user's orders")
    public ResponseEntity<List<OrderResponse>> getOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrders(principal.getName()));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order detail")
    public ResponseEntity<OrderResponse> getOrderById(Principal principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getName(), orderId));
    }
}
