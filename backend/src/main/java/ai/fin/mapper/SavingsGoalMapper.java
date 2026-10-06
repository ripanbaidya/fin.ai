package ai.fin.mapper;

import ai.fin.dto.savingsgoal.GoalProgressResponse;
import ai.fin.entities.SavingsGoal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class SavingsGoalMapper {

    private SavingsGoalMapper() {
    }

    public static GoalProgressResponse toProgressResponse(SavingsGoal goal) {
        BigDecimal saved = goal.getSavedAmount();
        BigDecimal target = goal.getTargetAmount();
        BigDecimal remaining = target.subtract(saved).max(BigDecimal.ZERO);

        double pct = saved.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getDeadline());

        return GoalProgressResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .targetAmount(target)
                .savedAmount(saved)
                .remainingAmount(remaining)
                .progressPercentage(Math.min(pct, 100.0))
                .daysRemaining(daysRemaining)
                .status(goal.getStatus())
                .deadline(goal.getDeadline())
                .build();
    }
}
