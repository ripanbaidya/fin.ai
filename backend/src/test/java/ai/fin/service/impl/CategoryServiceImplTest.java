package ai.fin.service.impl;

import ai.fin.dto.category.CategoryResponse;
import ai.fin.dto.category.CreateCategoryRequest;
import ai.fin.dto.category.UpdateCategoryRequest;
import ai.fin.entities.Category;
import ai.fin.entities.User;
import ai.fin.enums.CategoryType;
import ai.fin.repository.CategoryRepository;
import ai.fin.repository.UserRepository;
import ai.fin.shared.exception.ErrorCode;
import ai.fin.shared.exception.types.CategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-123");
        testUser.setEmail("test@walletiq.ai");

        otherUser = new User();
        otherUser.setId("user-999");
        otherUser.setEmail("other@walletiq.ai");
    }

    @Test
    @DisplayName("getAll should return visible categories for the user")
    void shouldReturnAllVisibleCategories() {
        Category defaultCategory = new Category();
        defaultCategory.setId("cat-default");
        defaultCategory.setName("Food");
        defaultCategory.setCategoryType(CategoryType.EXPENSE);
        defaultCategory.setUser(null); // system default

        Category userCategory = new Category();
        userCategory.setId("cat-user");
        userCategory.setName("Crypto");
        userCategory.setCategoryType(CategoryType.EXPENSE);
        userCategory.setUser(testUser);

        when(categoryRepository.findAllVisibleToUser("user-123", CategoryType.EXPENSE))
                .thenReturn(List.of(defaultCategory, userCategory));

        List<CategoryResponse> result = categoryService.getAll("user-123", CategoryType.EXPENSE);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Food");
        assertThat(result.get(0).isDefault()).isTrue();
        assertThat(result.get(1).name()).isEqualTo("Crypto");
        assertThat(result.get(1).isDefault()).isFalse();

        verify(categoryRepository).findAllVisibleToUser("user-123", CategoryType.EXPENSE);
    }

    @Test
    @DisplayName("create should save new category using JPA proxy reference without querying user DB")
    void shouldCreateCategorySuccessfully() {
        CreateCategoryRequest request = new CreateCategoryRequest("Freelance", CategoryType.INCOME);

        when(categoryRepository.isNameTaken("user-123", CategoryType.INCOME, "Freelance"))
                .thenReturn(false);
        when(userRepository.getReferenceById("user-123")).thenReturn(testUser);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId("cat-new-1");
            return c;
        });

        CategoryResponse response = categoryService.create("user-123", request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("cat-new-1");
        assertThat(response.name()).isEqualTo("Freelance");
        assertThat(response.categoryType()).isEqualTo(CategoryType.INCOME);
        assertThat(response.isDefault()).isFalse();

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getUser().getId()).isEqualTo("user-123");
        verify(userRepository).getReferenceById("user-123");
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("create should throw CATEGORY_ALREADY_EXISTS when duplicate name exists")
    void shouldThrowWhenCreatingDuplicateCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Food", CategoryType.EXPENSE);

        when(categoryRepository.isNameTaken("user-123", CategoryType.EXPENSE, "Food"))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.create("user-123", request))
                .isInstanceOf(CategoryException.class)
                .satisfies(ex -> assertThat(((CategoryException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CATEGORY_ALREADY_EXISTS));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should update owned category successfully")
    void shouldUpdateCategorySuccessfully() {
        Category existing = new Category();
        existing.setId("cat-1");
        existing.setName("Old Food");
        existing.setCategoryType(CategoryType.EXPENSE);
        existing.setUser(testUser);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Dining", CategoryType.EXPENSE);

        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(existing));
        when(categoryRepository.isNameTakenExcludingId("user-123", CategoryType.EXPENSE, "Dining", "cat-1"))
                .thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.update("user-123", "cat-1", request);

        assertThat(response.name()).isEqualTo("Dining");
        assertThat(response.categoryType()).isEqualTo(CategoryType.EXPENSE);
        verify(categoryRepository).save(existing);
    }

    @Test
    @DisplayName("update should reject modifying system default category")
    void shouldRejectUpdatingSystemDefaultCategory() {
        Category defaultCat = new Category();
        defaultCat.setId("cat-default");
        defaultCat.setName("Groceries");
        defaultCat.setUser(null); // system default

        UpdateCategoryRequest request = new UpdateCategoryRequest("New Groceries", CategoryType.EXPENSE);

        when(categoryRepository.findById("cat-default")).thenReturn(Optional.of(defaultCat));

        assertThatThrownBy(() -> categoryService.update("user-123", "cat-default", request))
                .isInstanceOf(CategoryException.class)
                .satisfies(ex -> assertThat(((CategoryException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CATEGORY_ACCESS_DENIED));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("update should reject modifying category owned by another user")
    void shouldRejectUpdatingAnotherUsersCategory() {
        Category othersCat = new Category();
        othersCat.setId("cat-other");
        othersCat.setName("Other Food");
        othersCat.setUser(otherUser);

        UpdateCategoryRequest request = new UpdateCategoryRequest("Hacked Food", CategoryType.EXPENSE);

        when(categoryRepository.findById("cat-other")).thenReturn(Optional.of(othersCat));

        assertThatThrownBy(() -> categoryService.update("user-123", "cat-other", request))
                .isInstanceOf(CategoryException.class)
                .satisfies(ex -> assertThat(((CategoryException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CATEGORY_ACCESS_DENIED));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete should remove owned category successfully")
    void shouldDeleteCategorySuccessfully() {
        Category existing = new Category();
        existing.setId("cat-1");
        existing.setName("Custom Transport");
        existing.setUser(testUser);

        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(existing));

        categoryService.delete("user-123", "cat-1");

        verify(categoryRepository).delete(existing);
    }

    @Test
    @DisplayName("delete should reject deleting system default category")
    void shouldRejectDeletingSystemDefaultCategory() {
        Category defaultCat = new Category();
        defaultCat.setId("cat-default");
        defaultCat.setName("Groceries");
        defaultCat.setUser(null);

        when(categoryRepository.findById("cat-default")).thenReturn(Optional.of(defaultCat));

        assertThatThrownBy(() -> categoryService.delete("user-123", "cat-default"))
                .isInstanceOf(CategoryException.class)
                .satisfies(ex -> assertThat(((CategoryException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CATEGORY_ACCESS_DENIED));

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("findById should return category when found and throw when not found")
    void shouldFindByIdOrThrow() {
        Category category = new Category();
        category.setId("cat-1");

        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(category));
        when(categoryRepository.findById("cat-missing")).thenReturn(Optional.empty());

        assertThat(categoryService.findById("cat-1")).isSameAs(category);

        assertThatThrownBy(() -> categoryService.findById("cat-missing"))
                .isInstanceOf(CategoryException.class)
                .satisfies(ex -> assertThat(((CategoryException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
