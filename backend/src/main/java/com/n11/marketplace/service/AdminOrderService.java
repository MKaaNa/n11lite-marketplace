package com.n11.marketplace.service;

import com.n11.marketplace.dto.response.AdminOrderResponse;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderStatus;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.OrderMapper;
import com.n11.marketplace.repository.OrderRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminOrderService {

    private static final Logger log = LoggerFactory.getLogger(AdminOrderService.class);

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;

    public AdminOrderService(
            OrderRepository orderRepository,
            EmailService emailService,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.orderMapper = orderMapper;
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> listAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toAdminResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminOrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        return orderMapper.toAdminResponse(order);
    }

    @Transactional
    public AdminOrderResponse updateOrderStatus(Long orderId, String statusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        OrderStatus newStatus = parseAdminStatus(statusStr);
        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        log.info("Admin updated order {} status to {}", orderId, newStatus);

        try {
            if (newStatus == OrderStatus.SHIPPED) {
                emailService.sendOrderShippedEmail(saved.getUser().getEmail(), saved.getId());
            } else if (newStatus == OrderStatus.DELIVERED) {
                emailService.sendOrderDeliveredEmail(saved.getUser().getEmail(), saved.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to send status notification email for order {}", orderId);
        }

        return orderMapper.toAdminResponse(saved);
    }

    private OrderStatus parseAdminStatus(String statusStr) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + statusStr, HttpStatus.BAD_REQUEST);
        }

        if (status != OrderStatus.SHIPPED
                && status != OrderStatus.DELIVERED
                && status != OrderStatus.CANCELLED) {
            throw new BusinessException("Status not allowed for admin update: " + statusStr, HttpStatus.BAD_REQUEST);
        }

        return status;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean allowed = switch (current) {
            case PAYMENT_PENDING -> next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED;
            default -> false;
        };

        if (!allowed) {
            throw new BusinessException(
                    "Cannot transition order from " + current.name() + " to " + next.name(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
