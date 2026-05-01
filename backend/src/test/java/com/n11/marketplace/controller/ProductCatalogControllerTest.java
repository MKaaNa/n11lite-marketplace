package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.dto.response.CategoryResponse;
import com.n11.marketplace.dto.response.ProductDetailResponse;
import com.n11.marketplace.dto.response.ProductSummaryResponse;
import com.n11.marketplace.dto.response.StoreResponse;
import com.n11.marketplace.exception.BusinessException;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.service.ProductCatalogService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductCatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    void categoriesShouldReturnCategoryList() throws Exception {
        when(productCatalogService.getActiveCategories())
                .thenReturn(List.of(new CategoryResponse(1L, "Electronics", "electronics")));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].slug").value("electronics"));
    }

    @Test
    void productsShouldReturnPagedProductSummaries() throws Exception {
        ProductSummaryResponse product = createProductSummary();
        PageRequest pageRequest = PageRequest.of(0, 12);
        when(productCatalogService.getProducts(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product), pageRequest, 1));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Wireless Bluetooth Headphones"))
                .andExpect(jsonPath("$.content[0].category.slug").value("electronics"))
                .andExpect(jsonPath("$.content[0].store.name").value("TechStore"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(12))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void productsShouldPassCategoryAndSearchParamsToService() throws Exception {
        when(productCatalogService.getProducts(eq("electronics"), eq("watch"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 12), 0));

        mockMvc.perform(get("/api/products")
                        .param("category", "electronics")
                        .param("search", "watch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void productBySlugShouldReturnDetail() throws Exception {
        when(productCatalogService.getProductBySlug("wireless-bluetooth-headphones"))
                .thenReturn(createProductDetail());

        mockMvc.perform(get("/api/products/wireless-bluetooth-headphones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wireless Bluetooth Headphones"))
                .andExpect(jsonPath("$.slug").value("wireless-bluetooth-headphones"))
                .andExpect(jsonPath("$.images[0].imageUrl").value("https://example.com/image-1.jpg"));
    }

    @Test
    void productBySlugShouldReturnNotFoundWhenServiceThrows() throws Exception {
        when(productCatalogService.getProductBySlug("missing-product"))
                .thenThrow(new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/products/missing-product"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    private ProductSummaryResponse createProductSummary() {
        CategoryResponse category = new CategoryResponse(1L, "Electronics", "electronics");
        StoreResponse store = new StoreResponse(1L, "TechStore", "https://example.com/store.png",
                new BigDecimal("4.70"), true);

        return new ProductSummaryResponse(
                1L,
                "Wireless Bluetooth Headphones",
                "wireless-bluetooth-headphones",
                new BigDecimal("1299.90"),
                45,
                "BESTSELLER",
                "https://example.com/image-1.jpg",
                category,
                store);
    }

    private ProductDetailResponse createProductDetail() {
        ProductSummaryResponse summary = createProductSummary();
        return new ProductDetailResponse(
                summary.getId(),
                summary.getName(),
                summary.getSlug(),
                "Comfortable wireless headphones with long battery life.",
                summary.getPrice(),
                summary.getStock(),
                180L,
                950L,
                summary.getBadge(),
                summary.getCategory(),
                summary.getStore(),
                List.of(new com.n11.marketplace.dto.response.ProductImageResponse(
                        1L,
                        "https://example.com/image-1.jpg",
                        1)));
    }
}
