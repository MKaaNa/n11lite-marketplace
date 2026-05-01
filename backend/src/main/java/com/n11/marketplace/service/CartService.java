package com.n11.marketplace.service;

import com.n11.marketplace.dto.request.AddToCartRequest;
import com.n11.marketplace.dto.request.UpdateCartItemRequest;
import com.n11.marketplace.dto.response.CartResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.User;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.CartMapper;
import com.n11.marketplace.repository.CartItemRepository;
import com.n11.marketplace.repository.CartRepository;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public CartResponse getCart(String userEmail) {
        Cart cart = findOrCreateCart(userEmail);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String userEmail, AddToCartRequest request) {
        Cart cart = findOrCreateCart(userEmail);
        Product product = productRepository.findById(request.getProductId())
                .filter(Product::isActive)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (item == null) {
            checkStock(request.getQuantity(), product.getStock());
            item = new CartItem(cart, product, request.getQuantity());
            cart.addItem(item);
        } else {
            int newQuantity = item.getQuantity() + request.getQuantity();
            checkStock(newQuantity, product.getStock());
            item.setQuantity(newQuantity);
        }

        cartRepository.save(cart);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String userEmail, Long itemId, UpdateCartItemRequest request) {
        Cart cart = findCartByUserEmail(userEmail);
        CartItem item = findCartItemForUserCart(cart, itemId);

        checkStock(request.getQuantity(), item.getProduct().getStock());
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userEmail, Long itemId) {
        Cart cart = findCartByUserEmail(userEmail);
        CartItem item = findCartItemForUserCart(cart, itemId);

        cart.removeItem(item);
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(String userEmail) {
        Cart cart = findCartByUserEmail(userEmail);
        cart.getItems().clear();
        cartRepository.save(cart);

        return cartMapper.toResponse(cart);
    }

    private Cart findOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    User user = findUserByEmail(userEmail);
                    return cartRepository.save(new Cart(user));
                });
    }

    private Cart findCartByUserEmail(String userEmail) {
        findUserByEmail(userEmail);
        return cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new BusinessException("Cart item not found", HttpStatus.NOT_FOUND));
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.UNAUTHORIZED));
    }

    private CartItem findCartItemForUserCart(Cart cart, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("Cart item not found", HttpStatus.NOT_FOUND));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BusinessException("Cart item not found", HttpStatus.NOT_FOUND);
        }

        return item;
    }

    private void checkStock(Integer quantity, Integer stock) {
        if (quantity > stock) {
            throw new BusinessException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
        }
    }
}
