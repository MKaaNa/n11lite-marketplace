package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.response.InitiatePaymentResponse;
import com.n11.marketplace.dto.response.PaymentCallbackResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.OrderItem;
import com.n11.marketplace.entity.OrderStatus;
import com.n11.marketplace.entity.Payment;
import com.n11.marketplace.entity.PaymentStatus;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.payment.IyzicoPaymentClient;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.OrderRepository;
import com.n11.marketplace.repository.PaymentRepository;
import com.n11.marketplace.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IyzicoPaymentClient iyzicoPaymentClient;

    @Mock
    private CouponService couponService;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                paymentRepository,
                orderRepository,
                cartRepository,
                productRepository,
                iyzicoPaymentClient,
                couponService);
    }

    @Test
    void initiateCheckoutRejectsAnotherUsersOrderAsNotFound() {
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(orderRepository.findByIdAndUserEmail(50L, "user@test.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.initiateCheckout("user@test.com", 50L));

        assertEquals("Order not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void initiateCheckoutRejectsNonPaymentPendingOrder() {
        User user = createUser();
        Order order = createOrder(50L, user, createProduct(10L, 5), 2);
        order.setStatus(OrderStatus.PAID);
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(orderRepository.findByIdAndUserEmail(50L, user.getEmail())).thenReturn(Optional.of(order));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.initiateCheckout(user.getEmail(), 50L));

        assertEquals("Order is not waiting for payment", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void initiateCheckoutCreatesPendingPayment() {
        User user = createUser();
        Order order = createOrder(50L, user, createProduct(10L, 5), 2);
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(orderRepository.findByIdAndUserEmail(50L, user.getEmail())).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(order.getId())).thenReturn(Optional.empty());
        when(iyzicoPaymentClient.initializeCheckout(order)).thenReturn(
                new IyzicoPaymentClient.CheckoutInitializeResult(
                        true,
                        "checkout-token",
                        "https://sandbox-checkout.iyzico.com/pay",
                        null));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "id", 80L);
            return payment;
        });

        InitiatePaymentResponse response = paymentService.initiateCheckout(user.getEmail(), order.getId());

        assertEquals(50L, response.getOrderId());
        assertEquals(80L, response.getPaymentId());
        assertEquals("checkout-token", response.getToken());
        assertEquals("https://sandbox-checkout.iyzico.com/pay", response.getPaymentPageUrl());
        assertEquals("PENDING", response.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void initiateCheckoutRejectsWhenIyzicoIsNotConfigured() {
        when(iyzicoPaymentClient.isConfigured()).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.initiateCheckout("user@test.com", 50L));

        assertEquals("Iyzico is not configured", exception.getMessage());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void successfulCallbackMarksPaymentSuccessAndOrderPaid() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        Cart cart = createCart(1L, user, product);
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(true, "SUCCESS", null));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        PaymentCallbackResponse response = paymentService.handleCallback("checkout-token");

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("PAID", response.getOrderStatus());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(PaymentStatus.SUCCESS, order.getPaymentStatus());
    }

    @Test
    void successfulCallbackDecreasesStock() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(true, "SUCCESS", null));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        paymentService.handleCallback("checkout-token");

        assertEquals(3, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void successfulCallbackClearsCart() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        Cart cart = createCart(1L, user, product);
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(true, "SUCCESS", null));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        paymentService.handleCallback("checkout-token");

        assertEquals(0, cart.getItems().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void successfulCallbackIncrementsCouponUsedCount() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        order.setCouponCode("N11WELCOME");
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(true, "SUCCESS", null));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        paymentService.handleCallback("checkout-token");

        verify(couponService).markCouponUsed("N11WELCOME");
    }

    @Test
    void failedCallbackMarksPaymentFailedAndOrderFailed() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(false, "FAILURE", "failed"));

        PaymentCallbackResponse response = paymentService.handleCallback("checkout-token");

        assertEquals("FAILED", response.getStatus());
        assertEquals("FAILED", response.getOrderStatus());
        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertEquals(PaymentStatus.FAILED, order.getPaymentStatus());
    }

    @Test
    void failedCallbackDoesNotDecreaseStock() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(false, "FAILURE", "failed"));

        paymentService.handleCallback("checkout-token");

        assertEquals(5, product.getStock());
        verify(productRepository, never()).save(product);
    }

    @Test
    void failedCallbackDoesNotClearCart() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(false, "FAILURE", "failed"));

        paymentService.handleCallback("checkout-token");

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void failedCallbackDoesNotIncrementCouponUsedCount() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        order.setCouponCode("N11WELCOME");
        Payment payment = createPayment(80L, order, "checkout-token");
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));
        when(iyzicoPaymentClient.retrieveCheckoutResult("checkout-token")).thenReturn(
                new IyzicoPaymentClient.CheckoutResult(false, "FAILURE", "failed"));

        paymentService.handleCallback("checkout-token");

        verify(couponService, never()).markCouponUsed("N11WELCOME");
    }

    @Test
    void alreadySuccessfulCallbackDoesNotIncrementCouponAgain() {
        User user = createUser();
        Product product = createProduct(10L, 5);
        Order order = createOrder(50L, user, product, 2);
        order.setCouponCode("N11WELCOME");
        Payment payment = createPayment(80L, order, "checkout-token");
        payment.setStatus(PaymentStatus.SUCCESS);
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("checkout-token")).thenReturn(Optional.of(payment));

        paymentService.handleCallback("checkout-token");

        verify(couponService, never()).markCouponUsed("N11WELCOME");
    }

    @Test
    void callbackWithUnknownTokenReturnsNotFound() {
        when(iyzicoPaymentClient.isConfigured()).thenReturn(true);
        when(paymentRepository.findByIyzicoToken("missing-token")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.handleCallback("missing-token"));

        assertEquals("Payment not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    private User createUser() {
        User user = new User("user@test.com", "hash", "Test User", "5551112233", Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Product createProduct(Long id, Integer stock) {
        Category category = new Category("Books", "books");
        Store store = new Store("BookNest");
        Product product = new Product(
                "Modern Java Guide",
                "modern-java-guide",
                new BigDecimal("100.00"),
                stock,
                category,
                store);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private Order createOrder(Long id, User user, Product product, Integer quantity) {
        Order order = new Order(user, "Istanbul, Turkey");
        BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.addItem(new OrderItem(product, product.getName(), product.getSlug(), product.getPrice(), quantity, lineTotal));
        order.setTotalAmount(lineTotal);
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }

    private Payment createPayment(Long id, Order order, String token) {
        Payment payment = new Payment(order, order.getTotalAmount());
        payment.setIyzicoToken(token);
        ReflectionTestUtils.setField(payment, "id", id);
        return payment;
    }

    private Cart createCart(Long id, User user, Product product) {
        Cart cart = new Cart(user);
        ReflectionTestUtils.setField(cart, "id", id);
        CartItem item = new CartItem(cart, product, 2);
        ReflectionTestUtils.setField(item, "id", 100L);
        cart.addItem(item);
        return cart;
    }
}
