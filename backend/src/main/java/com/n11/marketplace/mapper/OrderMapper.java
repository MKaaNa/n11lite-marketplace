package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.AdminOrderResponse;
import com.n11.marketplace.dto.response.OrderItemResponse;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getShippingAddress(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getDiscountAmount(),
                order.getCreatedAt(),
                items);
    }

    public AdminOrderResponse toAdminResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new AdminOrderResponse(
                order.getId(),
                order.getUser().getEmail(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getDiscountAmount(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                items);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getProductSlug(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal());
    }
}
