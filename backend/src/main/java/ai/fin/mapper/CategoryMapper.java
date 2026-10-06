package ai.fin.mapper;

import ai.fin.dto.category.CategoryResponse;
import ai.fin.entities.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId().toString(),
                category.getName(),
                category.getCategoryType(),
                category.getUser() == null
        );
    }
}