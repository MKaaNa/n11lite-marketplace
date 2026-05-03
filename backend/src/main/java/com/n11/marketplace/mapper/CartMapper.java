package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.CartItemResponse;
import com.n11.marketplace.dto.response.CartResponse;
import com.n11.marketplace.entity.Cart;
import com.n11.marketplace.entity.CartItem;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductImage;
import com.n11.marketplace.entity.ProductVariant;
import com.n11.marketplace.repository.ProductImageRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    private final ProductImageRepository productImageRepository;

    public CartMapper(ProductImageRepository productImageRepository) {
        this.productImageRepository = productImageRepository;
    }

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            CartItemResponse itemResponse = toItemResponse(item);
            itemResponses.add(itemResponse);
            totalAmount = totalAmount.add(itemResponse.getLineTotal());
        }

        return new CartResponse(cart.getId(), itemResponses, totalAmount);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        String imageUrl = findMainImageUrl(product.getId());
        ProductVariant variant = item.getProductVariant();

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                imageUrl,
                variant != null ? variant.getId() : null,
                variant != null ? variant.getVariantType() : null,
                variant != null ? variant.getVariantValue() : null,
                product.getPrice(),
                item.getQuantity(),
                lineTotal);
    }

    private String findMainImageUrl(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        if (images.isEmpty()) {
            return null;
        }

        return images.get(0).getImageUrl();
    }
}
