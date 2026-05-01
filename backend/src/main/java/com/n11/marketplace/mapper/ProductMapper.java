package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.ProductDetailResponse;
import com.n11.marketplace.dto.response.ProductImageResponse;
import com.n11.marketplace.dto.response.ProductSummaryResponse;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final StoreMapper storeMapper;
    private final ProductImageMapper productImageMapper;

    public ProductMapper(
            CategoryMapper categoryMapper,
            StoreMapper storeMapper,
            ProductImageMapper productImageMapper) {
        this.categoryMapper = categoryMapper;
        this.storeMapper = storeMapper;
        this.productImageMapper = productImageMapper;
    }

    public ProductSummaryResponse toSummary(Product product, String mainImageUrl) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                product.getStock(),
                getBadgeName(product),
                mainImageUrl,
                categoryMapper.toResponse(product.getCategory()),
                storeMapper.toResponse(product.getStore()));
    }

    public ProductDetailResponse toDetail(Product product, List<ProductImage> images) {
        List<ProductImageResponse> imageResponses = images.stream()
                .map(productImageMapper::toResponse)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getSoldCount(),
                product.getViewCount(),
                getBadgeName(product),
                categoryMapper.toResponse(product.getCategory()),
                storeMapper.toResponse(product.getStore()),
                imageResponses);
    }

    private String getBadgeName(Product product) {
        if (product.getBadge() == null) {
            return null;
        }
        return product.getBadge().name();
    }
}
