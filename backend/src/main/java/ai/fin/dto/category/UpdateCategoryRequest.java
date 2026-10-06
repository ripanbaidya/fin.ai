package ai.fin.dto.category;

import ai.fin.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        name = "UpdateCategoryRequest",
        description = "Request payload for updating an existing category"
)
public record UpdateCategoryRequest(

        @Schema(
                description = "Updated name of the category",
                example = "Food",
                minLength = 1,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Category name must not be blank")
        @Size(min = 1, max = 100, message = "Category name must be between 1 and 100 characters")
        String name,

        @Schema(
                description = "Type of the category",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Category type is required")
        CategoryType categoryType

) {
}