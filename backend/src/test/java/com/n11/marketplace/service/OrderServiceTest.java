package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.request.CreateOrderRequest;
import com.n11.marketplace.dto.response.OrderResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Order;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.OrderMapper;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.OrderRepository;
import com.n11.marketplace.repository.UserRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, cartRepository, userRepository, new OrderMapper());
    }

    @Test
    void createOrderCreatesOrderFromCart() {
        User user = createUser("user@test.com");
        Product product = createProduct(10L, 5);
        Cart cart = createCart(1L, user);
        cart.addItem(createCartItem(100L, cart, product, 2));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 50L);
            return order;
        });

        OrderResponse response = orderService.createOrder(
                user.getEmail(),
                new CreateOrderRequest("Istanbul, Turkey"));

        assertEquals(50L, response.getId());
        assertEquals("PAYMENT_PENDING", response.getStatus());
        assertEquals("PENDING", response.getPaymentStatus());
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        assertEquals("Modern Java Guide", response.getItems().get(0).getProductName());
        assertEquals("modern-java-guide", response.getItems().get(0).getProductSlug());
        assertEquals(new BigDecimal("100.00"), response.getItems().get(0).getUnitPrice());
        assertEquals(2, response.getItems().get(0).getQuantity());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrderRejectsEmptyCart() {
        User user = createUser("user@test.com");
        Cart cart = createCart(1L, user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(user.getEmail(), new CreateOrderRequest("Address")));

        assertEquals("Cart is empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderRejectsInsufficientStock() {
        User user = createUser("user@test.com");
        Product product = createProduct(10L, 1);
        Cart cart = createCart(1L, user);
        cart.addItem(createCartItem(100L, cart, product, 2));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(user.getEmail(), new CreateOrderRequest("Address")));

        assertEquals("Quantity exceeds stock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderDoesNotDecreaseProductStock() {
        User user = createUser("user@test.com");
        Product product = createProduct(10L, 5);
        Cart cart = createCart(1L, user);
        cart.addItem(createCartItem(100L, cart, product, 2));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(user.getEmail(), new CreateOrderRequest("Address"));

        assertEquals(5, product.getStock());
    }

    @Test
    void createOrderDoesNotClearCart() {
        User user = createUser("user@test.com");
        Product product = createProduct(10L, 5);
        Cart cart = createCart(1L, user);
        cart.addItem(createCartItem(100L, cart, product, 2));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(user.getEmail(), new CreateOrderRequest("Address"));

        assertEquals(1, cart.getItems().size());
    }

    @Test
    void getOrdersReturnsOnlyAuthenticatedUsersOrders() {
        User user = createUser("user@test.com");
        Order order = createOrder(50L, user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByUserEmailOrderByCreatedAtDesc(user.getEmail()))
                .thenReturn(List.of(order));

        List<OrderResponse> response = orderService.getOrders(user.getEmail());

        assertEquals(1, response.size());
        assertEquals(50L, response.get(0).getId());
        verify(orderRepository).findByUserEmailOrderByCreatedAtDesc(user.getEmail());
    }

    @Test
    void getOrderByIdReturnsOwnOrder() {
        User user = createUser("user@test.com");
        Order order = createOrder(50L, user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserEmail(50L, user.getEmail())).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(user.getEmail(), 50L);

        assertEquals(50L, response.getId());
        assertEquals("Address", response.getShippingAddress());
    }

    @Test
    void getOrderByIdRejectsAnotherUsersOrderAsNotFound() {
        User user = createUser("user@test.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(orderRepository.findByIdAndUserEmail(99L, user.getEmail())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> orderService.getOrderById(user.getEmail(), 99L));

        assertEquals("Order not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    private User createUser(String email) {
        User user = new User(email, "hash", "Test User", "5551112233", Role.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Cart createCart(Long id, User user) {
        Cart cart = new Cart(user);
        ReflectionTestUtils.setField(cart, "id", id);
        return cart;
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

    private CartItem createCartItem(Long id, Cart cart, Product product, Integer quantity) {
        CartItem item = new CartItem(cart, product, quantity);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }

    private Order createOrder(Long id, User user) {
        Product product = createProduct(10L, 5);
        Order order = new Order(user, "Address");
        order.addItem(new com.n11.marketplace.entity.OrderItem(
                product,
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                2,
                new BigDecimal("200.00")));
        order.setTotalAmount(new BigDecimal("200.00"));
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }
}
