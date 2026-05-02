package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.response.AdminOrderResponse;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.entity.OrderStatus;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.OrderMapper;
import com.n11.marketplace.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmailService emailService;

    private AdminOrderService adminOrderService;

    @BeforeEach
    void setUp() {
        adminOrderService = new AdminOrderService(orderRepository, emailService, new OrderMapper());
    }

    @Test
    void listAllOrdersReturnsAllOrders() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(order));

        List<AdminOrderResponse> result = adminOrderService.listAllOrders();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("user@test.com", result.get(0).getUserEmail());
    }

    @Test
    void getOrderByIdReturnsOrder() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        AdminOrderResponse result = adminOrderService.getOrderById(1L);

        assertEquals(1L, result.getId());
        assertEquals("PAID", result.getStatus());
    }

    @Test
    void getOrderByIdThrows404ForMissingOrder() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.getOrderById(99L));

        assertEquals("Order not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateStatusPaidToShippedSucceeds() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "SHIPPED");

        assertEquals("SHIPPED", result.getStatus());
        verify(emailService).sendOrderShippedEmail("user@test.com", 1L);
    }

    @Test
    void updateStatusShippedToDeliveredSucceeds() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "DELIVERED");

        assertEquals("DELIVERED", result.getStatus());
        verify(emailService).sendOrderDeliveredEmail("user@test.com", 1L);
    }

    @Test
    void updateStatusPaymentPendingToCancelledSucceeds() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
        verify(emailService, never()).sendOrderShippedEmail(any(), any());
        verify(emailService, never()).sendOrderDeliveredEmail(any(), any());
    }

    @Test
    void updateStatusPaidToCancelledSucceeds() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void updateStatusShippedToCancelledSucceeds() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "CANCELLED");

        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    void invalidTransitionDeliveredToShippedRejects() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.updateOrderStatus(1L, "SHIPPED"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void invalidTransitionFailedToShippedRejects() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.FAILED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.updateOrderStatus(1L, "SHIPPED"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void invalidTransitionPaymentPendingToShippedRejects() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.updateOrderStatus(1L, "SHIPPED"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void unknownStatusStringRejects() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.updateOrderStatus(1L, "BOGUS"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void disallowedAdminStatusPaidRejects() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> adminOrderService.updateOrderStatus(1L, "PAID"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void emailFailureDoesNotRollBackStatusUpdate() {
        User user = createUser("user@test.com");
        Order order = createOrder(1L, user, OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.doThrow(new RuntimeException("mail server down"))
                .when(emailService).sendOrderShippedEmail("user@test.com", 1L);

        AdminOrderResponse result = adminOrderService.updateOrderStatus(1L, "SHIPPED");

        assertEquals("SHIPPED", result.getStatus());
    }

    private User createUser(String email) {
        User user = new User(email, "hash", "Test User", "5551112233", Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Order createOrder(Long id, User user, OrderStatus status) {
        Category category = new Category("Books", "books");
        Store store = new Store("BookNest");
        Product product = new Product(
                "Modern Java Guide",
                "modern-java-guide",
                new BigDecimal("100.00"),
                5,
                category,
                store);
        ReflectionTestUtils.setField(product, "id", 10L);

        Order order = new Order(user, "Istanbul, Turkey");
        order.addItem(new OrderItem(
                product,
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                2,
                new BigDecimal("200.00")));
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus(status);
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }
}
