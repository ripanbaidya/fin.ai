package ai.fin.service;

import ai.fin.dto.dashboard.DashboardResponse;

import java.time.YearMonth;

public interface DashboardService {

    /*
     * Generates a dashboard snapshot for the given month.
     * This Includes:
     * - Total income, expense, and net balance
     * - Category-wise breakdown for income and expenses
     * - Daily transaction trends
     * - Top 5 expenses
     *
     * All calculations are scoped to the currently authenticated user.
     *
     * @param userId ID of the authenticated user
     * @param month target month
     * @return dashboard response containing aggregated financial insights
     */
    DashboardResponse getDashboard(String userId, YearMonth month);
}
