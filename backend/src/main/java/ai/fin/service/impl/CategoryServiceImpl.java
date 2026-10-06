package ai.fin.service.impl;

import ai.fin.dto.category.CategoryResponse;
import ai.fin.dto.category.CreateCategoryRequest;
import ai.fin.dto.category.UpdateCategoryRequest;
import ai.fin.entities.Category;
import ai.fin.enums.CategoryType;
import ai.fin.mapper.CategoryMapper;
import ai.fin.repository.CategoryRepository;
import ai.fin.repository.UserRepository;
import ai.fin.service.CategoryService;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.CategoryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#userId + '_' + (#type != null ? #type.name() : 'ALL')")
    public List<CategoryResponse> getAll(String userId, CategoryType type) {
        log.debug("Fetching categories visible to userId='{}', type='{}'", userId, type);

        return categoryRepository.findAllVisibleToUser(userId, type)
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse create(String userId, CreateCategoryRequest request) {
        String name = request.name().trim();
        CategoryType categoryType = request.categoryType();

        log.debug("Creating category for userId='{}', name='{}', type='{}'", userId, name, categoryType);

        if (categoryRepository.isNameTaken(userId, categoryType, name)) {
            throw new CategoryException(
                    ErrorCode.CATEGORY_ALREADY_EXISTS,
                    "A category named '%s' already exists".formatted(name)
            );
        }

        Category category = new Category();
        category.setName(name);
        category.setCategoryType(categoryType);
        category.setUser(userRepository.getReferenceById(userId));

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id='{}' for userId='{}'", savedCategory.getId(), userId);

        return CategoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse update(String userId, String categoryId, UpdateCategoryRequest request) {
        Category category = findOwnedCategory(categoryId, userId);

        String newName = request.name().trim();
        CategoryType newType = request.categoryType() != null ? request.categoryType() : category.getCategoryType();

        log.debug("Updating category id='{}' for userId='{}' to name='{}', type='{}'",
                categoryId, userId, newName, newType);

        boolean nameChanged = !category.getName().equalsIgnoreCase(newName);
        boolean typeChanged = newType != category.getCategoryType();

        if (nameChanged || typeChanged) {
            if (categoryRepository.isNameTakenExcludingId(userId, newType, newName, categoryId)) {
                throw new CategoryException(
                        ErrorCode.CATEGORY_ALREADY_EXISTS,
                        "A category named '%s' already exists".formatted(newName)
                );
            }
        }

        category.setName(newName);
        category.setCategoryType(newType);

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category id='{}' updated successfully for userId='{}'", categoryId, userId);

        return CategoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void delete(String userId, String categoryId) {
        log.debug("Deleting category id='{}' for userId='{}'", categoryId, userId);

        Category category = findOwnedCategory(categoryId, userId);
        categoryRepository.delete(category);

        log.info("Category id='{}' deleted successfully for userId='{}'", categoryId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * Finds a category and validates ownership against the requesting user.
     * Throws 404 NOT_FOUND if the category doesn't exist, and 403 ACCESS_DENIED if the category
     * is a system default or belongs to another user.
     */
    private Category findOwnedCategory(String categoryId, String userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getUser() == null) {
            throw new CategoryException(
                    ErrorCode.CATEGORY_ACCESS_DENIED,
                    "System default categories cannot be modified or deleted."
            );
        }

        if (!userId.equals(category.getUser().getId())) {
            throw new CategoryException(
                    ErrorCode.CATEGORY_ACCESS_DENIED,
                    "You do not have permission to modify this category."
            );
        }

        return category;
    }
}
