package ai.fin.service;

import java.time.YearMonth;

public interface BudgetAlertService {

    /**
     * Called after any 'EXPENSE' transaction is created or updated.
     * Checks if the user has a budget for this category and month and logs a warning if the
     * threshold or limit is breached.
     * Intentionally does NOT throw — budget alerts must never block a transaction from being saved.
     *
     * @param userId     identifier of the user
     * @param categoryId identifier of the category
     * @param month      month of the budget
     */
    void checkAndAlert(String userId, String categoryId, YearMonth month);
}
