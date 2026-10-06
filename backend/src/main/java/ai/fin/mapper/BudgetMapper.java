package ai.fin.mapper;

import ai.fin.dto.budget.BudgetResponse;
import ai.fin.entities.Budget;

public final class BudgetMapper {

    private BudgetMapper() {
    }

    public static BudgetResponse toResponse(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getName(),
                budget.getCategory().getId(),
                budget.getMonth(),
                budget.getLimitAmount(),
                budget.getAlertThreshold()
        );
    }
}