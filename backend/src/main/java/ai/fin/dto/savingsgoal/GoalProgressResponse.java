package ai.fin.dto.savingsgoal;

import ai.fin.enums.GoalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(
        name = "GoalProgressResponse",
        description = "Response payload representing the progress and status of a savings goal"
)
public record GoalProgressResponse(

        @Schema(description = "Unique identifier of the savings goal")
        String id,

        @Schema(
                description = "Title of the savings goal defined by the user",
                example = "Buy MacBook Pro"
        )
        String title,

        @Schema(
                description = "Total amount the user aims to save for this goal",
                example = "150000.00"
        )
        BigDecimal targetAmount,

        @Schema(
                description = "Total amount already saved toward the goal",
                example = "50000.00"
        )
        BigDecimal savedAmount,

        @Schema(
                description = "Remaining amount required to achieve the goal",
                example = "100000.00"
        )
        BigDecimal remainingAmount,


        @Schema(
                description = "Progress percentage of the savings goal (0–100)",
                example = "33.33"
        )
        double progressPercentage,

        @Schema(
                description = "Number of days remaining until the goal deadline. Negative value indicates the deadline has passed.",
                example = "45"
        )
        long daysRemaining,

        @Schema(
                description = "Current status of the savings goal",
                example = "IN_PROGRESS",
                allowableValues = {"IN_PROGRESS", "ACHIEVED", "FAILED"}
        )
        GoalStatus status,

        @Schema(
                description = "Deadline date by which the goal should be achieved",
                example = "2026-12-31"
        )
        LocalDate deadline

) {
}