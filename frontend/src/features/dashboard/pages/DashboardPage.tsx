import { useState } from "react";
import { dashboardService } from "../dashboardService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";

import MonthNavigator from "../../../shared/components/layout/MonthNavigator";
import SummaryCards from "../components/SummaryCards";
import DailyTrendChart from "../components/DailyTrendChart";
import CategoryBreakdown from "../components/CategoryBreakdown";
import TopExpenses from "../components/TopExpenses";

/* Helpers */
const currentMonth = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
};

/* Page */
export default function DashboardPage() {
  const [month, setMonth] = useState(currentMonth);

  const { data, isLoading, error, refetch, isFetching } = useAppQuery({
    queryKey: ["dashboard", month],
    queryFn: () => dashboardService.getDashboardData(month),
    placeholderData: (prev) => prev,
  });

  const dashboard = data?.data;
  const isRefetching = isFetching && !isLoading;

  return (
    <div className="mx-auto w-full min-w-0 max-w-6xl space-y-6">
      {/* ── Header ── */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div className="min-w-0">
          <h1 className="text-lg sm:text-xl font-semibold text-gray-900 truncate">
            Dashboard
          </h1>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            Overview of your financial activity for this month.
          </p>
        </div>

        {/* RIGHT — Month Navigator */}
        <div className="w-full sm:w-auto flex items-center justify-between sm:justify-end gap-3 flex-shrink-0">
          <span className="text-xs text-gray-400 hidden sm:inline whitespace-nowrap">
            Viewing
          </span>

          <div className="w-full sm:w-auto">
            <MonthNavigator month={month} onChange={setMonth} />
          </div>
        </div>
      </div>

      {/* ── Loading (initial) ── */}
      {isLoading ? (
        <div className="flex justify-center py-16 sm:py-24">
          <Spinner />
        </div>
      ) : error ? (
        <QueryError error={error} onRetry={refetch} />
      ) : !dashboard ? null : (
        <div
          className={`space-y-6 transition-opacity duration-150 ${
            isRefetching ? "opacity-60" : "opacity-100"
          }`}
        >
          {/* ── Summary Cards ── */}
          <div className="w-full min-w-0">
            <SummaryCards summary={dashboard.summary} />
          </div>

          {/* ── Daily income and expenses ── */}
          <section
            aria-labelledby="daily-trend-title"
            className="bg-white border border-gray-200 rounded-lg p-4 sm:p-5 min-w-0"
          >
            <div className="mb-4">
              <h2
                id="daily-trend-title"
                className="text-sm font-medium text-gray-700"
              >
                Income vs. expenses
              </h2>
              <p className="text-xs text-gray-400 mt-1">
                Daily cash flow for the selected month
              </p>
            </div>
            <div className="w-full min-w-0">
              <DailyTrendChart data={dashboard.dailyTrend} />
            </div>
          </section>

          {/* ── Category Breakdowns ── */}
          <div className="grid min-w-0 grid-cols-1 gap-4 xl:grid-cols-2">
            <section
              aria-label="Expenses by category"
              className="min-w-0 rounded-lg border border-gray-200 bg-white p-4 sm:p-5"
            >
              <CategoryBreakdown
                title="Expense by Category"
                data={dashboard.expenseByCategory}
                type="expense"
              />
            </section>

            <section
              aria-label="Income by category"
              className="min-w-0 rounded-lg border border-gray-200 bg-white p-4 sm:p-5"
            >
              <CategoryBreakdown
                title="Income by Category"
                data={dashboard.incomeByCategory}
                type="income"
              />
            </section>
          </div>

          {/* ── Top Expenses ── */}
          <section
            aria-labelledby="top-expenses-title"
            className="min-w-0 rounded-lg border border-gray-200 bg-white p-4 sm:p-5"
          >
            <h2
              id="top-expenses-title"
              className="text-sm font-medium text-gray-700"
            >
              Top 5 Expenses
            </h2>
            <div className="mt-1 w-full min-w-0">
              <TopExpenses data={dashboard.topExpenses} />
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
