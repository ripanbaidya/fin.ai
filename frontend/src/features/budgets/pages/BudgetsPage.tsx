import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { budgetService } from "../budgetService";
import { categoryService } from "../../categories/categoryService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";

import BudgetMonthNav from "../components/BudgetMonthNav";
import BudgetForm from "../components/BudgetForm";
import BudgetCard from "../components/BudgetCard";

import type { CreateBudgetRequest, BudgetResponse } from "../budget.types";

// Helpers

const currentMonth = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
};

// Page

export default function BudgetsPage() {
  const queryClient = useQueryClient();

  const [month, setMonth] = useState(currentMonth);
  const [showForm, setShowForm] = useState(false);
  const [submitError, setSubmitError] = useState<AppError | null>(null);

  // Budgets for selected month
  const budgetQuery = useAppQuery({
    queryKey: ["budgets", month],
    queryFn: () => budgetService.getByMonth(month),
    placeholderData: (prev) => prev,
  });

  // Expense categories for the form
  const categoryQuery = useAppQuery({
    queryKey: ["categories", "EXPENSE"],
    queryFn: () => categoryService.getAll("EXPENSE"),
  });

  // Create
  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: (data: CreateBudgetRequest) => budgetService.create(data),
    onSuccess: () => {
      setShowForm(false);
      setSubmitError(null);
      queryClient.invalidateQueries({ queryKey: ["budgets", month] });
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => budgetService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["budgets", month] });
    },
  });

  // Derived
  const budgets = budgetQuery.data?.data ?? [];
  const expenseCategories = (categoryQuery.data?.data ?? []).filter(
    (c) => c.categoryType === "EXPENSE",
  );

  const isRefetching = budgetQuery.isFetching && !budgetQuery.isLoading;

  return (
    <div className="max-w-3xl mx-auto space-y-5">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Budgets</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            Set spending limits per category each month.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <BudgetMonthNav month={month} onChange={setMonth} />
          <button
            onClick={() => {
              setSubmitError(null);
              setShowForm(true);
            }}
            className="text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors whitespace-nowrap"
          >
            + New Budget
          </button>
        </div>
      </div>

      {/* Content */}
      {budgetQuery.isLoading ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : budgetQuery.error ? (
        <QueryError
          error={budgetQuery.error}
          onRetry={() => budgetQuery.refetch()}
        />
      ) : (
        <div
          className={`space-y-3 transition-opacity duration-150 ${isRefetching ? "opacity-60" : "opacity-100"}`}
        >
          {budgets.length === 0 ? (
            <div className="bg-white border border-gray-200 rounded-lg py-16 text-center">
              <p className="text-sm text-gray-400 mb-3">
                No budgets set for{" "}
                <span className="font-medium text-gray-600">{month}</span>.
              </p>
              <button
                onClick={() => setShowForm(true)}
                className="text-sm text-black underline underline-offset-2"
              >
                Create your first budget
              </button>
            </div>
          ) : (
            <>
              {/* Summary line */}
              <p className="text-xs text-gray-400 text-right">
                {budgets.length} budget{budgets.length === 1 ? "" : "s"} for{" "}
                {month}
              </p>

              {/* Budget cards */}
              {budgets.map((budget: BudgetResponse) => (
                <BudgetCard
                  key={budget.id}
                  budget={budget}
                  onDelete={(id) => remove(id)}
                  isDeleting={isDeleting}
                />
              ))}

              {/* Hint */}
              <p className="text-xs text-gray-400 text-center pt-1">
                Click ▶ on any budget to see spending status
              </p>
            </>
          )}
        </div>
      )}

      {/* Create panel */}
      {showForm && <BudgetForm
        open={showForm}
        onClose={() => setShowForm(false)}
        onSubmit={(data) => create(data)}
        isPending={isCreating}
        submitError={submitError}
        categories={expenseCategories}
        defaultMonth={month}
      />}
    </div>
  );
}
