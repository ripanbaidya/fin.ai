package ai.fin.service.impl;

import ai.fin.entities.Budget;
import ai.fin.enums.NotificationType;
import ai.fin.repository.BudgetRepository;
import ai.fin.repository.TransactionRepository;
import ai.fin.service.BudgetAlertService;
import ai.fin.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetAlertServiceImpl implements BudgetAlertService {

    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    @Override
    public void checkAndAlert(String userId, String categoryId, YearMonth month) {
        if (categoryId == null) return;

        Optional<Budget> budgetOpt = budgetRepository.findByUser_IdAndCategoryIdAndMonth(userId, categoryId, month);
        if (budgetOpt.isEmpty()) return;

        Budget budget = budgetOpt.get();

        BigDecimal totalSpent = transactionRepository.sumExpensesByCategoryAndMonth(
                userId, categoryId, month.atDay(1), month.atEndOfMonth());
        BigDecimal limit = budget.getLimitAmount();
        double usagePercentage = totalSpent.divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        log.debug("Budget check: userId: {} category: {} month: {} spent: {} limit: {} usage: {}%",
                userId, categoryId, month, totalSpent, limit, String.format("%.1f", usagePercentage));

        String categoryName = budget.getCategory() != null ? budget.getCategory().getName() : "General";

        Map<String, String> data = new HashMap<>();
        data.put("budgetId", budget.getId());
        data.put("categoryId", categoryId);
        data.put("month", month.toString());

        if (totalSpent.compareTo(limit) > 0) {
            log.warn("[BUDGET EXCEEDED] userId: {} category: {} month: {} spent: {} limit: {}",
                    userId, categoryId, month, totalSpent, limit);

            String title = "Budget Exceeded!";
            String message = String.format("You have exceeded your monthly budget for %s (%s). Spent: %s, Limit: %s.",
                    categoryName, month.format(MONTH_LABEL), totalSpent, limit);
            data.put("type", NotificationType.BUDGET_EXCEEDED.name());

            notificationService.sendNotification(userId, title, message, NotificationType.BUDGET_EXCEEDED, data);
        } else if (usagePercentage >= budget.getAlertThreshold()) {
            log.warn("[BUDGET THRESHOLD] userId: {} category: {} month: {} usage: {}% threshold: {}%",
                    userId, categoryId, month, String.format("%.1f", usagePercentage),
                    budget.getAlertThreshold());

            String title = "Budget Alert";
            String message = String.format("Budget warning for %s (%s): %.1f%% used (threshold %d%%).",
                    categoryName, month.format(MONTH_LABEL), usagePercentage, budget.getAlertThreshold());
            data.put("type", NotificationType.BUDGET_WARNING.name());

            notificationService.sendNotification(userId, title, message, NotificationType.BUDGET_WARNING, data);
        }
    }
}
