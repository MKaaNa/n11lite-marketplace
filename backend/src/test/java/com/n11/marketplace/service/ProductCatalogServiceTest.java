package com.n11.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.n11.marketplace.dto.response.CategoryResponse;
import com.n11.marketplace.dto.response.ProductDetailResponse;
import com.n11.marketplace.dto.response.ProductSummaryResponse;
import com.n11.marketplace.entity.Category;
import com.n11.marketplace.entity.Product;
import com.n11.marketplace.entity.ProductImage;
import com.n11.marketplace.entity.Store;
import com.n11.marketplace.enums.ProductBadge;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.mapper.CategoryMapper;
import com.n11.marketplace.mapper.ProductImageMapper;
import com.n11.marketplace.mapper.ProductMapper;
import com.n11.marketplace.mapper.StoreMapper;
import com.n11.marketplace.repository.CategoryRepository;
import com.n11.marketplace.repository.ProductImageRepository;
import com.n11.marketplace.repository.ProductRepository;
import com.n11.marketplace.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    private ProductCatalogService productCatalogService;

    @BeforeEach
    void setUp() {
        CategoryMapper categoryMapper = new CategoryMapper();
        StoreMapper storeMapper = new StoreMapper();
        ProductImageMapper productImageMapper = new ProductImageMapper();
        ProductMapper productMapper = new ProductMapper(categoryMapper, storeMapper, productImageMapper);

        productCatalogService = new ProductCatalogService(
                productRepository,
                categoryRepository,
                productImageRepository,
                productVariantRepository,
                productMapper,
                categoryMapper);
    }

    @Test
    void getProductsWithoutFiltersReturnsActiveProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct(1L);
        ProductImage image = createImage(1L, product, "https://example.com/image-1.jpg", 1);
        when(productRepository.findByActiveTrue(pageable)).thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId())))
                .thenReturn(List.of(image));

        Page<ProductSummaryResponse> response = productCatalogService.getProducts(null, null, pageable);

        assertEquals(1, response.getTotalElements());
        ProductSummaryResponse productResponse = response.getContent().get(0);
        assertEquals(product.getName(), productResponse.getName());
        assertEquals(product.getSlug(), productResponse.getSlug());
        assertEquals("BESTSELLER", productResponse.getBadge());
        assertEquals(image.getImageUrl(), productResponse.getMainImageUrl());
        verify(productRepository).findByActiveTrue(pageable);
        verify(productImageRepository).findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId()));
    }

    @Test
    void getProductsWithCategoryUsesCategoryMethod() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct(1L);
        when(productRepository.findByActiveTrueAndCategorySlug("electronics", pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId())))
                .thenReturn(List.of());

        Page<ProductSummaryResponse> response = productCatalogService.getProducts("electronics", null, pageable);

        assertEquals(1, response.getTotalElements());
        assertNull(response.getContent().get(0).getMainImageUrl());
        verify(productRepository).findByActiveTrueAndCategorySlug("electronics", pageable);
        verify(productImageRepository).findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId()));
    }

    @Test
    void getProductsWithSearchUsesSearchMethod() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct(1L);
        when(productRepository.findByActiveTrueAndNameContainingIgnoreCase("watch", pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId())))
                .thenReturn(List.of());

        Page<ProductSummaryResponse> response = productCatalogService.getProducts(null, "watch", pageable);

        assertEquals(1, response.getTotalElements());
        verify(productRepository).findByActiveTrueAndNameContainingIgnoreCase("watch", pageable);
        verify(productImageRepository).findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId()));
    }

    @Test
    void getProductsWithCategoryAndSearchUsesCombinedMethod() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createProduct(1L);
        when(productRepository.findByActiveTrueAndCategorySlugAndNameContainingIgnoreCase(
                "electronics",
                "watch",
                pageable))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productImageRepository.findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId())))
                .thenReturn(List.of());

        Page<ProductSummaryResponse> response = productCatalogService.getProducts("electronics", "watch", pageable);

        assertEquals(1, response.getTotalElements());
        verify(productRepository).findByActiveTrueAndCategorySlugAndNameContainingIgnoreCase(
                "electronics",
                "watch",
                pageable);
        verify(productImageRepository).findByProductIdInOrderByProductIdAscDisplayOrderAsc(List.of(product.getId()));
    }

    @Test
    void getProductBySlugReturnsDetailWithImages() {
        Product product = createProduct(1L);
        ProductImage firstImage = createImage(1L, product, "https://example.com/image-1.jpg", 1);
        ProductImage secondImage = createImage(2L, product, "https://example.com/image-2.jpg", 2);
        when(productRepository.findBySlug(product.getSlug())).thenReturn(Optional.of(product));
        when(productImageRepository.findByProductIdOrderByDisplayOrderAsc(product.getId()))
                .thenReturn(List.of(firstImage, secondImage));

        ProductDetailResponse response = productCatalogService.getProductBySlug(product.getSlug());

        assertEquals(product.getName(), response.getName());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getViewCount(), response.getViewCount());
        assertEquals(2, response.getImages().size());
        assertEquals(firstImage.getImageUrl(), response.getImages().get(0).getImageUrl());
    }

    @Test
    void getProductBySlugThrowsWhenProductMissing() {
        when(productRepository.findBySlug("missing-product")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCatalogService.getProductBySlug("missing-product"));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(productImageRepository, never()).findByProductIdOrderByDisplayOrderAsc(1L);
    }

    @Test
    void getProductBySlugThrowsWhenProductInactive() {
        Product product = createProduct(1L);
        product.setActive(false);
        when(productRepository.findBySlug(product.getSlug())).thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCatalogService.getProductBySlug(product.getSlug()));

        assertEquals("Product not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(productImageRepository, never()).findByProductIdOrderByDisplayOrderAsc(product.getId());
    }

    @Test
    void getActiveCategoriesReturnsMappedCategories() {
        Category electronics = createCategory(1L);
        Category books = new Category("Books", "books");
        ReflectionTestUtils.setField(books, "id", 2L);
        when(categoryRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(books, electronics));

        List<CategoryResponse> response = productCatalogService.getActiveCategories();

        assertEquals(2, response.size());
        assertEquals("Books", response.get(0).getName());
        assertEquals("electronics", response.get(1).getSlug());
    }

    private Product createProduct(Long id) {
        Category category = createCategory(10L);
        Store store = createStore(20L);
        Product product = new Product(
                "Wireless Bluetooth Headphones",
                "wireless-bluetooth-headphones",
                new BigDecimal("1299.90"),
                45,
                category,
                store);
        ReflectionTestUtils.setField(product, "id", id);
        product.setDescription("Comfortable wireless headphones with long battery life.");
        product.setSoldCount(180L);
        product.setViewCount(950L);
        product.setBadge(ProductBadge.BESTSELLER);
        return product;
    }

    private Category createCategory(Long id) {
        Category category = new Category("Electronics", "electronics");
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

    private Store createStore(Long id) {
        Store store = new Store("TechStore");
        ReflectionTestUtils.setField(store, "id", id);
        store.setLogoUrl("https://example.com/store.png");
        store.setRating(new BigDecimal("4.70"));
        store.setOfficial(true);
        return store;
    }

    private ProductImage createImage(Long id, Product product, String imageUrl, Integer displayOrder) {
        ProductImage image = new ProductImage(product, imageUrl, displayOrder);
        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }
}
