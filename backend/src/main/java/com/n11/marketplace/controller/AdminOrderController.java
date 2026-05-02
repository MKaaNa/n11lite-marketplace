package com.n11.marketplace.controller;

import com.n11.marketplace.dto.request.UpdateOrderStatusRequest;
import com.n11.marketplace.dto.response.AdminOrderResponse;
import com.n11.marketplace.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Admin - Orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    @Operation(summary = "List all orders (admin)")
    public ResponseEntity<List<AdminOrderResponse>> listAllOrders() {
        return ResponseEntity.ok(adminOrderService.listAllOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order detail (admin)")
    public ResponseEntity<AdminOrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminOrderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status (admin)")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(orderId, request.getStatus()));
    }
}
