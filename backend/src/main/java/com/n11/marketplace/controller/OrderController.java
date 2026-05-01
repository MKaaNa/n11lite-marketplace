package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.CreateOrderRequest;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.service.OrderService;
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
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(principal.getName(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getOrders(principal.getName()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(Principal principal, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getName(), orderId));
    }
}
