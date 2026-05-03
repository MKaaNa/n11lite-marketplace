package com.n11.marketplace.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class ProductCatalogSort {

    private ProductCatalogSort() {
    }

    /**
     * Builds a page request with page/size from {@code pageable} and a whitelist sort.
     * Any sort coming from the raw Pageable is ignored so clients cannot inject arbitrary fields.
     */
    public static Pageable apply(Pageable pageable, String sortKey) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), resolveSort(sortKey));
    }

    private static Sort resolveSort(String sortKey) {
        if (sortKey == null || sortKey.isBlank()) {
            return Sort.by("id").ascending();
        }
        return switch (sortKey.trim().toLowerCase()) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "best_selling" -> Sort.by("soldCount").descending();
            case "recommended" -> Sort.by("id").ascending();
            default -> Sort.by("id").ascending();
        };
    }
}
