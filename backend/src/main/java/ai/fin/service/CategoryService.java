package ai.fin.service;

import ai.fin.dto.category.CategoryResponse;
import ai.fin.dto.category.CreateCategoryRequest;
import ai.fin.dto.category.UpdateCategoryRequest;
import ai.fin.entities.Category;
import ai.fin.enums.CategoryType;

import java.util.List;

public interface CategoryService {

    /**
     * Retrieves all categories visible to the specified user.
     * Includes both system default and user-defined categories.
     */
    List<CategoryResponse> getAll(String userId, CategoryType type);

    /**
     * Creates a new category for the specified user.
     * Ensures that no duplicate category exists with the same name (case-insensitive) and
     * type for the user or system defaults.
     */
    CategoryResponse create(String userId, CreateCategoryRequest request);

    /**
     * Updates an existing category owned by the specified user.
     * Supports updating name and type.
     */
    CategoryResponse update(String userId, String categoryId, UpdateCategoryRequest request);

    /**
     * Deletes a category owned by the specified user.
     */
    void delete(String userId, String categoryId);

    /**
     * Retrieves a category by its ID.
     */
    Category findById(String id);

}