package ai.fin.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "CategoryBreakdownItem",
        description = "Represents the total amount spent for a specific category"
)
public record CategoryBreakdownItem(

        @Schema(description = "Name of the category", example = "Food")
        String categoryName,

        @Schema(description = "Total amount spent in the category", example = "3250.75")
        BigDecimal amount

) {
}