package ai.fin.service.impl;

import ai.fin.entities.Budget;
import ai.fin.entities.SavingsGoal;
import ai.fin.repository.BudgetRepository;
import ai.fin.repository.SavingsGoalRepository;
import ai.fin.repository.TransactionRepository;
import ai.fin.service.ContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContextServiceImpl implements ContextService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsGoalRepository savingsGoalRepository;

    @Override
    @Transactional(readOnly = true)
    public String buildBudgetContext(String userId) {
        YearMonth month = YearMonth.now();
        List<Budget> budgets = budgetRepository.findByUser_IdAndMonth(userId, month);

        if (budgets.isEmpty()) {
            return "No budgets found for this month.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== BUDGET STATUS (").append(month).append(") ===\n");

        for (Budget b : budgets) {
            BigDecimal spent = transactionRepository.sumExpensesByCategoryAndMonth(
                    userId, b.getCategory().getId(), month.atDay(1), month.atEndOfMonth());
            BigDecimal limit = b.getLimitAmount();
            BigDecimal remaining = limit.subtract(spent).max(BigDecimal.ZERO);

            double pct = spent.divide(limit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

            sb.append(
                    String.format("- %s: spent ₹%.2f of ₹%.2f limit (%.1f%% used, ₹%.2f remaining)%s\n",
                            b.getCategory().getName(),
                            spent, limit, pct, remaining,
                            pct > 100 ? " ⚠️ EXCEEDED" : (pct >= b.getAlertThreshold() ? " ⚠️ NEAR LIMIT" : ""))
            );
        }

        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public String buildSavingsGoalContext(String userId) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUser_Id(userId);
        if (goals.isEmpty()) {
            return "No savings goals set.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== SAVINGS GOALS ===\n");

        for (SavingsGoal g : goals) {
            BigDecimal saved = g.getSavedAmount();
            BigDecimal target = g.getTargetAmount();

            double pct = saved.divide(target, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            long daysLeft = ChronoUnit.DAYS
                    .between(LocalDate.now(), g.getDeadline());

            sb.append(
                    String.format("- %s: saved ₹%.2f of ₹%.2f (%.1f%%) | status: %s | deadline: %s (%d days %s)\n",
                            g.getTitle(), saved, target, pct, g.getStatus(), g.getDeadline(),
                            Math.abs(daysLeft), daysLeft >= 0 ? "remaining" : "overdue")
            );
        }

        return sb.toString();
    }
}
