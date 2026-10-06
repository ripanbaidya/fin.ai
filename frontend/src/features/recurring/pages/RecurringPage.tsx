import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { recurringService } from "../recurringService";
import { categoryService } from "../../categories/categoryService";
import { paymentModeService } from "../../payment-modes/paymentModeService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";

import RecurringForm from "../components/RecurringForm";
import RecurringCard from "../components/RecurringCard";
import ForecastPanel from "../components/ForeCastPanel";

import type {
  RecurringTransactionResponse,
  CreateRecurringTransactionRequest,
  UpdateRecurringTransactionRequest,
} from "../recurring.types";

export default function RecurringPage() {
  const queryClient = useQueryClient();

  const [editing, setEditing] = useState<
    RecurringTransactionResponse | null | false
  >(null);
  const [submitError, setSubmitError] = useState<AppError | null>(null);
  const [activeTab, setActiveTab] = useState<"rules" | "forecast">("rules");

  const isFormOpen = editing !== null;
  const isEditMode = editing !== null && editing !== false;

  const rulesQuery = useAppQuery({
    queryKey: ["recurring"],
    queryFn: () => recurringService.getAll(),
  });

  const categoryIncomeQuery = useAppQuery({
    queryKey: ["categories", "INCOME"],
    queryFn: () => categoryService.getAll("INCOME"),
  });

  const categoryExpenseQuery = useAppQuery({
    queryKey: ["categories", "EXPENSE"],
    queryFn: () => categoryService.getAll("EXPENSE"),
  });

  const paymentModeQuery = useAppQuery({
    queryKey: ["payment-modes"],
    queryFn: () => paymentModeService.getAll(),
  });

  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: (data: CreateRecurringTransactionRequest) =>
      recurringService.create(data),
    onSuccess: () => {
      closeForm();
      queryClient.invalidateQueries({ queryKey: ["recurring"] });
      queryClient.invalidateQueries({ queryKey: ["recurring-forecast"] });
      toast.success("Recurring rule created");
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  const { mutate: update, isPending: isUpdating } = useAppMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateRecurringTransactionRequest;
    }) => recurringService.update(id, data),
    onSuccess: () => {
      closeForm();
      queryClient.invalidateQueries({ queryKey: ["recurring"] });
      queryClient.invalidateQueries({ queryKey: ["recurring-forecast"] });
      toast.success("Recurring rule updated");
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  const { mutate: deactivate, isPending: isDeactivating } = useAppMutation({
    mutationFn: (id: string) => recurringService.deactivate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["recurring"] });
      queryClient.invalidateQueries({ queryKey: ["recurring-forecast"] });
      toast.success("Recurring rule stopped");
    },
    onError: (err: AppError) => toast.error(err.message),
  });

  const openCreate = () => {
    setSubmitError(null);
    setEditing(false);
  };

  const openEdit = (rule: RecurringTransactionResponse) => {
    setSubmitError(null);
    setEditing(rule);
  };

  const closeForm = () => {
    setEditing(null);
    setSubmitError(null);
  };

  const rules = rulesQuery.data?.data ?? [];

  const allCategories = [
    ...(categoryIncomeQuery.data?.data ?? []),
    ...(categoryExpenseQuery.data?.data ?? []),
  ];

  const paymentModes = paymentModeQuery.data?.data ?? [];
  const isPending = isCreating || isUpdating;

  const totalMonthlyIncome = rules
    .filter((r) => r.type === "INCOME")
    .reduce((sum, r) => {
      const multiplier =
        r.frequency === "DAILY"
          ? 30
          : r.frequency === "WEEKLY"
            ? 4
            : r.frequency === "MONTHLY"
              ? 1
              : 1 / 12;
      return sum + r.amount * multiplier;
    }, 0);

  const totalMonthlyExpense = rules
    .filter((r) => r.type === "EXPENSE")
    .reduce((sum, r) => {
      const multiplier =
        r.frequency === "DAILY"
          ? 30
          : r.frequency === "WEEKLY"
            ? 4
            : r.frequency === "MONTHLY"
              ? 1
              : 1 / 12;
      return sum + r.amount * multiplier;
    }, 0);

  const fmtAmt = (n: number) =>
    new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      maximumFractionDigits: 0,
    }).format(n);

  return (
    <div className="max-w-4xl mx-auto w-full px-4 sm:px-6 lg:px-0 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div className="min-w-0">
          <h1 className="text-lg sm:text-xl font-semibold text-gray-900 truncate">
            Recurring Transactions
          </h1>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            Automate your regular income and expenses.
          </p>
        </div>

        <button
          onClick={openCreate}
          className="w-full sm:w-auto text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors whitespace-nowrap"
        >
          + New Rule
        </button>
      </div>

      {/* Stats */}
      {rules.length > 0 && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-xs text-gray-400 mb-1">Est. Monthly Income</p>
            <p className="text-lg font-semibold text-green-600">
              {fmtAmt(totalMonthlyIncome)}
            </p>
          </div>

          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-xs text-gray-400 mb-1">Est. Monthly Expense</p>
            <p className="text-lg font-semibold text-red-600">
              {fmtAmt(totalMonthlyExpense)}
            </p>
          </div>

          <div className="bg-white border border-gray-200 rounded-lg p-4">
            <p className="text-xs text-gray-400 mb-1">Active Rules</p>
            <p className="text-lg font-semibold text-gray-900">
              {rules.length}
            </p>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="flex gap-1 border-b border-gray-200 overflow-x-auto">
        {(["rules", "forecast"] as const).map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px capitalize whitespace-nowrap ${
              activeTab === tab
                ? "border-black text-black"
                : "border-transparent text-gray-500 hover:text-gray-700"
            }`}
          >
            {tab === "rules" ? `Rules (${rules.length})` : "Forecast"}
          </button>
        ))}
      </div>

      {/* Content unchanged */}
      {activeTab === "rules" && (
        <>
          {rulesQuery.isLoading ? (
            <div className="flex justify-center py-16">
              <Spinner />
            </div>
          ) : rulesQuery.error ? (
            <QueryError
              error={rulesQuery.error}
              onRetry={() => rulesQuery.refetch()}
            />
          ) : rules.length === 0 ? (
            <div className="bg-white border border-gray-200 rounded-lg py-16 text-center px-4">
              <p className="text-sm text-gray-400 mb-3">
                No recurring rules yet.
              </p>
              <button
                onClick={openCreate}
                className="text-sm text-black underline underline-offset-2"
              >
                Create your first rule
              </button>
            </div>
          ) : (
            <div className="space-y-3">
              {rules.map((rule) => (
                <RecurringCard
                  key={rule.id}
                  rule={rule}
                  onEdit={openEdit}
                  onDeactivate={(id) => deactivate(id)}
                  isDeactivating={isDeactivating}
                />
              ))}
            </div>
          )}
        </>
      )}

      {activeTab === "forecast" && <ForecastPanel />}

      {/* Form unchanged */}
      {isFormOpen &&
        (isEditMode ? (
          <RecurringForm
            mode="edit"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: UpdateRecurringTransactionRequest) =>
              update({ id: (editing as RecurringTransactionResponse).id, data })
            }
            isPending={isPending}
            submitError={submitError}
            initialData={editing as RecurringTransactionResponse}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ) : (
          <RecurringForm
            mode="create"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: CreateRecurringTransactionRequest) => create(data)}
            isPending={isPending}
            submitError={submitError}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ))}
    </div>
  );
}
