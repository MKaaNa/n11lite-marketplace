package com.n11.marketplace.service;

import com.n11.marketplace.dto.response.CategoryResponse;
import com.n11.marketplace.dto.response.ProductDetailResponse;
import com.n11.marketplace.dto.response.ProductSummaryResponse;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductImage;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.CategoryMapper;
import com.n11.marketplace.mapper.ProductMapper;
import com.n11.marketplace.repository.CategoryRepository;
import com.n11.marketplace.repository.ProductImageRepository;
import com.n11.marketplace.repository.ProductRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public ProductCatalogService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductImageRepository productImageRepository,
            ProductMapper productMapper,
            CategoryMapper categoryMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    public Page<ProductSummaryResponse> getProducts(String categorySlug, String search, Pageable pageable) {
        boolean hasCategory = categorySlug != null && !categorySlug.isBlank();
        boolean hasSearch = search != null && !search.isBlank();

        Page<Product> products;
        if (hasCategory && hasSearch) {
            products = productRepository.findByActiveTrueAndCategorySlugAndNameContainingIgnoreCase(
                    categorySlug,
                    search,
                    pageable);
        } else if (hasCategory) {
            products = productRepository.findByActiveTrueAndCategorySlug(categorySlug, pageable);
        } else if (hasSearch) {
            products = productRepository.findByActiveTrueAndNameContainingIgnoreCase(search, pageable);
        } else {
            products = productRepository.findByActiveTrue(pageable);
        }

        if (products.isEmpty()) {
            return products.map(product -> productMapper.toSummary(product, null));
        }

        List<Long> productIds = products.getContent().stream()
                .map(Product::getId)
                .toList();
        List<ProductImage> images = productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(
                productIds);
        Map<Long, List<ProductImage>> imagesByProductId = images.stream()
                .collect(Collectors.groupingBy(image -> image.getProduct().getId()));

        return products.map(product -> {
            List<ProductImage> productImages = imagesByProductId.getOrDefault(product.getId(), List.of());
            String mainImageUrl = productImages.isEmpty() ? null : productImages.get(0).getImageUrl();
            return productMapper.toSummary(product, mainImageUrl);
        });
    }

    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .filter(Product::isActive)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId());
        return productMapper.toDetail(product, images);
    }

    public List<CategoryResponse> getActiveCategories() {
        List<Category> categories = categoryRepository.findByActiveTrueOrderByNameAsc();
        return categories.stream()
                .map(categoryMapper::toResponse)
                .toList();
    }
}
