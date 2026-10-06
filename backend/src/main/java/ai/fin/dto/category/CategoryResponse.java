package ai.fin.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import ai.fin.enums.CategoryType;

@Schema(
        name = "CategoryResponse",
        description = "Category response returned by the API"
)
public record CategoryResponse(

        @Schema(
                description = "Unique identifier of the category",
                example = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
        )
        String id,

        @Schema(description = "Name of the category", example = "Food")
        String name,

        @Schema(
                description = "Type of the category",
                example = "EXPENSE",
                allowableValues = {"INCOME", "EXPENSE"}
        )
        CategoryType categoryType,

        @Schema(
                description = "Indicates whether this category is a system default (true) or user-created (false)",
                example = "true"
        )
        boolean isDefault

) {
}