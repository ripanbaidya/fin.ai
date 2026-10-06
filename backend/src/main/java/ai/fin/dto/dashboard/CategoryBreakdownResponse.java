package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "CategoryBreakdownResponse",
        description = "Represents category-wise spending breakdown with percentage contribution"
)
public record CategoryBreakdownResponse(

        @Schema(description = "Name of the category", example = "Food")
        String categoryName,

        @Schema(description = "Total amount spent for the category", example = "3250.75")
        BigDecimal amount,

        @Schema(description = "Percentage contribution of this category to total spending", example = "24.5")
        double percentage

) {
}