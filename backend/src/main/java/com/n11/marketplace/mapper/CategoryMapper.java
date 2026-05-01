package com.n11.marketplace.mapper;

import com.n11.marketplace.dto.response.CategoryResponse;
import com.n11.marketplace.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug());
    }
}
