package com.n11.marketplace.controller;

import com.n11.marketplace.dto.response.CategoryResponse;
import com.n11.marketplace.dto.response.PageResponse;
import com.n11.marketplace.dto.response.ProductDetailResponse;
import com.n11.marketplace.dto.response.ProductSummaryResponse;
import com.n11.marketplace.service.ProductCatalogService;
import com.n11.marketplace.util.ProductCatalogSort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product Catalog")
@RestController
@RequestMapping("/api")
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    public ProductCatalogController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @Operation(summary = "List active categories")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(productCatalogService.getActiveCategories());
    }

    @Operation(summary = "List active products")
    @GetMapping("/products")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "recommended") String sort,
            @PageableDefault(size = 12) Pageable pageable) {
        Pageable safePageable = ProductCatalogSort.apply(pageable, sort);
        Page<ProductSummaryResponse> products = productCatalogService.getProducts(category, search, safePageable);
        return ResponseEntity.ok(PageResponse.from(products));
    }

    @Operation(summary = "Get product detail")
    @GetMapping("/products/{slug}")
    public ResponseEntity<ProductDetailResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productCatalogService.getProductBySlug(slug));
    }
}
