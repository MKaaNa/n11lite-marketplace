package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.request.AddToCartRequest;
import com.n11.marketplace.dto.request.UpdateCartItemRequest;
import com.n11.marketplace.dto.response.CartResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductVariant;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.enums.Role;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.CartMapper;
import com.n11.marketplace.repository.CartItemRepository;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.ProductImageRepository;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ProductVariantRepository;
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
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        CartMapper cartMapper = new CartMapper(productImageRepository);
        cartService = new CartService(
                cartRepository,
                cartItemRepository,
                userRepository,
                productRepository,
                productVariantRepository,
                cartMapper);
    }

    @Test
    void getCartCreatesCartWhenMissing() {
        User user = createUser();
        Cart savedCart = createCart(1L, user);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        CartResponse response = cartService.getCart(user.getEmail());

        assertEquals(1L, response.getId());
        assertEquals(0, response.getItems().size());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
    }

    @Test
    void addItemAddsNewItem() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 5);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductIdAndVariant(cart.getId(), product.getId(), null))
                .thenReturn(Optional.empty());
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()))
                .thenReturn(List.of());

        CartResponse response = cartService.addItem(user.getEmail(), new AddToCartRequest(product.getId(), 2, null));

        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("200.00"), response.getTotalAmount());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItemIncreasesExistingItemQuantity() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 5);
        CartItem item = createCartItem(100L, cart, product, 2);
        cart.addItem(item);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductIdAndVariant(cart.getId(), product.getId(), null))
                .thenReturn(Optional.of(item));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()))
                .thenReturn(List.of());

        CartResponse response = cartService.addItem(user.getEmail(), new AddToCartRequest(product.getId(), 3, null));

        assertEquals(5, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("500.00"), response.getTotalAmount());
    }

    @Test
    void addItemThrowsWhenQuantityExceedsStock() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 2);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductIdAndVariant(cart.getId(), product.getId(), null))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.addItem(user.getEmail(), new AddToCartRequest(product.getId(), 3, null)));

        assertEquals("Quantity exceeds stock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void addItemThrowsWhenFashionVariantMissing() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createFashionProduct(10L, 20);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.addItem(user.getEmail(), new AddToCartRequest(product.getId(), 1, null)));

        assertEquals("Variant selection is required for fashion products", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void addItemAddsFashionItemWithVariant() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createFashionProduct(10L, 20);
        ProductVariant variant = createVariant(91L, product, "BEDEN", "M", 5);
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productVariantRepository.findByIdAndActiveTrue(variant.getId())).thenReturn(Optional.of(variant));
        when(cartItemRepository.findByCartIdAndProductIdAndVariant(cart.getId(), product.getId(), variant.getId()))
                .thenReturn(Optional.empty());
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()))
                .thenReturn(List.of());

        CartResponse response = cartService.addItem(user.getEmail(), new AddToCartRequest(product.getId(), 2, variant.getId()));

        assertEquals(1, response.getItems().size());
        assertEquals("BEDEN", response.getItems().get(0).getVariantType());
        assertEquals("M", response.getItems().get(0).getVariantValue());
        assertEquals(2, response.getItems().get(0).getQuantity());
    }

    @Test
    void updateItemUpdatesQuantity() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 8);
        CartItem item = createCartItem(100L, cart, product, 2);
        cart.addItem(item);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()))
                .thenReturn(List.of());

        CartResponse response = cartService.updateItem(user.getEmail(), item.getId(), new UpdateCartItemRequest(4));

        assertEquals(4, response.getItems().get(0).getQuantity());
        verify(cartItemRepository).save(item);
    }

    @Test
    void updateItemThrowsWhenItemDoesNotBelongToUserCart() {
        User user = createUser();
        Cart userCart = createCart(1L, user);
        Cart otherCart = createCart(2L, user);
        Product product = createProduct(10L, 8);
        CartItem otherItem = createCartItem(100L, otherCart, product, 2);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(userCart));
        when(cartItemRepository.findById(otherItem.getId())).thenReturn(Optional.of(otherItem));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.updateItem(user.getEmail(), otherItem.getId(), new UpdateCartItemRequest(4)));

        assertEquals("Cart item not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateItemThrowsWhenQuantityExceedsStock() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 3);
        CartItem item = createCartItem(100L, cart, product, 2);
        cart.addItem(item);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.updateItem(user.getEmail(), item.getId(), new UpdateCartItemRequest(4)));

        assertEquals("Quantity exceeds stock", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void removeItemRemovesItem() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 8);
        CartItem item = createCartItem(100L, cart, product, 2);
        cart.addItem(item);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        CartResponse response = cartService.removeItem(user.getEmail(), item.getId());

        assertEquals(0, response.getItems().size());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
        verify(cartRepository).save(cart);
    }

    @Test
    void removeItemThrowsWhenItemDoesNotBelongToUserCart() {
        User user = createUser();
        Cart userCart = createCart(1L, user);
        Cart otherCart = createCart(2L, user);
        Product product = createProduct(10L, 8);
        CartItem otherItem = createCartItem(100L, otherCart, product, 2);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(userCart));
        when(cartItemRepository.findById(otherItem.getId())).thenReturn(Optional.of(otherItem));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.removeItem(user.getEmail(), otherItem.getId()));

        assertEquals("Cart item not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void clearCartClearsItems() {
        User user = createUser();
        Cart cart = createCart(1L, user);
        Product product = createProduct(10L, 8);
        cart.addItem(createCartItem(100L, cart, product, 2));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        CartResponse response = cartService.clearCart(user.getEmail());

        assertEquals(0, response.getItems().size());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
        verify(cartRepository).save(cart);
    }

    private User createUser() {
        User user = new User("user@test.com", "hash", "Test User", "5551112233", Role.USER);
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

    private Product createFashionProduct(Long id, Integer stock) {
        Category category = new Category("Moda", "fashion");
        Store store = new Store("ModaStore");
        Product product = new Product(
                "Basic Pamuklu Tişört",
                "basic-cotton-t-shirt",
                new BigDecimal("100.00"),
                stock,
                category,
                store);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private ProductVariant createVariant(Long id, Product product, String type, String value, Integer stock) {
        ProductVariant variant = new ProductVariant(product, type, value, stock);
        ReflectionTestUtils.setField(variant, "id", id);
        return variant;
    }

    private CartItem createCartItem(Long id, Cart cart, Product product, Integer quantity) {
        CartItem item = new CartItem(cart, product, null, quantity);
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }
}
