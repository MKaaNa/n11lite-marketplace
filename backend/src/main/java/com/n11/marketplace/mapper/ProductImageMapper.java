package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.ProductImageResponse;
import com.n11.marketplace.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper {

    public ProductImageResponse toResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getDisplayOrder());
    }
}
