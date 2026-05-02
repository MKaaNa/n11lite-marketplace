package com.n11.marketplace.service;

import com.n11.marketplace.dto.request.CreateOrderRequest;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Coupon;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.OrderMapper;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.OrderRepository;
import com.n11.marketplace.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CouponService couponService;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            OrderMapper orderMapper,
            CouponService couponService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.couponService = couponService;
    }

    @Transactional
    public OrderResponse createOrder(String userEmail, CreateOrderRequest request) {
        User user = findUser(userEmail);
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Cart is empty", HttpStatus.BAD_REQUEST));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order(user, request.getShippingAddress());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            validateProduct(product, cartItem.getQuantity());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);

            OrderItem orderItem = new OrderItem(
                    product,
                    product.getName(),
                    product.getSlug(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    lineTotal);
            order.addItem(orderItem);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponService.findValidCoupon(request.getCouponCode(), totalAmount);
            discountAmount = couponService.calculateDiscount(coupon, totalAmount);
            order.setCouponCode(coupon.getCode());
        }

        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount.subtract(discountAmount));
        Order savedOrder = orderRepository.save(order);
        log.info("Order created for user {}, order {}, total {}", userEmail, savedOrder.getId(), savedOrder.getTotalAmount());
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String userEmail) {
        findUser(userEmail);
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userEmail, Long orderId) {
        findUser(userEmail);
        Order order = orderRepository.findByIdAndUserEmail(orderId, userEmail)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        return orderMapper.toResponse(order);
    }

    private User findUser(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
    }

    private void validateProduct(Product product, Integer quantity) {
        if (product == null || !product.isActive()) {
            throw new BusinessException("Product not found", HttpStatus.NOT_FOUND);
        }

        if (quantity > product.getStock()) {
            throw new BusinessException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
        }
    }
}
