import { useState } from "react";
import { AppError } from "../../../api/errorParser";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";
import type { CategoryResponse } from "../../categories/category.types";
import type { CreateBudgetRequest } from "../budget.types";

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: CreateBudgetRequest) => void;
  isPending: boolean;
  submitError: AppError | null;
  // Only expense categories make sense for budgets
  categories: CategoryResponse[];
  // Pre-fill the month from the page's active month
  defaultMonth: string;
}

interface FormState {
  categoryId: string;
  limitAmount: string;
  alertThreshold: string;
}

const EMPTY: FormState = {
  categoryId: "",
  limitAmount: "",
  alertThreshold: "80",
};

const BudgetForm: React.FC<Props> = ({
  open,
  onClose,
  onSubmit,
  isPending,
  submitError,
  categories,
  defaultMonth,
}) => {
  const [form, setForm] = useState<FormState>(EMPTY);
  const [fieldErrors, setFieldErrors] =
    useState<Record<string, string> | null>(null);
  const serverFieldErrors = submitError?.isValidation
    ? submitError.toFieldErrorMap()
    : {};
  const errors = { ...serverFieldErrors, ...(fieldErrors ?? {}) };

  const set = (key: keyof FormState, value: string) => {
    setForm((p) => ({ ...p, [key]: value }));
    if (errors[key]) {
      setFieldErrors((previous) => ({
        ...serverFieldErrors,
        ...(previous ?? {}),
        [key]: "",
      }));
    }
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};
    if (!form.categoryId) errors.categoryId = "Select a category";
    const amt = Number(form.limitAmount);
    if (!form.limitAmount || isNaN(amt) || amt < 1) {
      errors.limitAmount = "Enter a limit of at least ₹1";
    }
    const thr = Number(form.alertThreshold);
    if (isNaN(thr) || thr < 1 || thr > 100) {
      errors.alertThreshold = "Threshold must be between 1 and 100";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!validate()) return;
    onSubmit({
      categoryId: form.categoryId,
      month: defaultMonth,
      limitAmount: Number(form.limitAmount),
      alertThreshold: Number(form.alertThreshold),
    });
  };

  if (!open) return null;

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/30 z-30" onClick={onClose} />

      {/* Panel */}
      <div className="fixed inset-y-0 right-0 w-full max-w-md bg-white shadow-xl z-40 flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <div>
            <h2 className="text-base font-semibold text-gray-900">
              New Budget
            </h2>
            <p className="text-xs text-gray-400 mt-0.5">
              Setting budget for{" "}
              <span className="font-medium text-gray-600">{defaultMonth}</span>
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 transition-colors text-xl leading-none"
          >
            ✕
          </button>
        </div>

        {/* Body */}
        <form
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto px-6 py-5 space-y-5"
        >
          {submitError && !submitError.isValidation && (
            <FormError error={submitError.message} />
          )}

          {/* Category */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">Category</label>
            <select
              value={form.categoryId}
              onChange={(e) => set("categoryId", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black bg-white"
            >
              <option value="">Select expense category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
            {categories.length === 0 && (
              <p className="text-xs text-amber-600 mt-1">
                No expense categories found. Create one first.
              </p>
            )}
            <FieldErrorMessage message={errors.categoryId} />
          </div>

          {/* Limit amount */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Monthly Limit (₹)
            </label>
            <input
              type="number"
              min="1"
              step="1"
              value={form.limitAmount}
              onChange={(e) => set("limitAmount", e.target.value)}
              placeholder="e.g. 5000"
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={errors.limitAmount} />
          </div>

          {/* Alert threshold */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Alert Threshold (%)
              <span className="text-gray-400 font-normal ml-1">
                — notify when spending crosses this %
              </span>
            </label>
            <input
              type="number"
              min="1"
              max="100"
              value={form.alertThreshold}
              onChange={(e) => set("alertThreshold", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={errors.alertThreshold} />
          </div>

          {/* Preview */}
          {form.limitAmount &&
            Number(form.limitAmount) > 0 &&
            form.alertThreshold && (
              <div className="bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 text-xs text-gray-500 space-y-1">
                <p>
                  Alert when spending exceeds{" "}
                  <span className="font-medium text-gray-700">
                    ₹
                    {(
                      (Number(form.limitAmount) * Number(form.alertThreshold)) /
                      100
                    ).toFixed(0)}
                  </span>{" "}
                  ({form.alertThreshold}% of limit)
                </p>
                <p>
                  Hard limit at{" "}
                  <span className="font-medium text-gray-700">
                    ₹{Number(form.limitAmount).toLocaleString("en-IN")}
                  </span>
                </p>
              </div>
            )}
        </form>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-gray-100 flex gap-2">
          <button
            onClick={handleSubmit}
            disabled={isPending}
            className="flex-1 py-2.5 bg-black text-white text-sm font-medium rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
          >
            {isPending && (
              <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            )}
            {isPending ? "Creating..." : "Create Budget"}
          </button>
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2.5 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            Cancel
          </button>
        </div>
      </div>
    </>
  );
};

export default BudgetForm;
