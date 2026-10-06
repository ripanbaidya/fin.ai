import { useState } from "react";
import { AppError } from "../../../api/errorParser";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";
import type { GoalProgressResponse } from "../savings.types";

interface Props {
  goal: GoalProgressResponse | null; // null = closed
  onClose: () => void;
  onSubmit: (goalId: string, amount: number) => void;
  isPending: boolean;
  submitError: AppError | null;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const ContributeModal: React.FC<Props> = ({
  goal,
  onClose,
  onSubmit,
  isPending,
  submitError,
}) => {
  const [amount, setAmount] = useState("");
  const [fieldError, setFieldError] = useState("");

  if (!goal) return null;

  const remaining = goal.remainingAmount;

  const validate = (): boolean => {
    const val = Number(amount);
    if (!amount || isNaN(val) || val <= 0) {
      setFieldError("Enter a valid amount greater than 0");
      return false;
    }
    setFieldError("");
    return true;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;
    onSubmit(goal.id, Number(amount));
  };

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/40 z-40" onClick={onClose} />

      {/* Modal */}
      <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
        <div className="bg-white rounded-xl shadow-xl w-full max-w-sm">
          {/* Header */}
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
            <h3 className="text-sm font-semibold text-gray-900">
              Add Contribution
            </h3>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-700 transition-colors leading-none"
            >
              ✕
            </button>
          </div>

          {/* Goal info */}
          <div className="px-5 pt-4 pb-2">
            <p className="text-sm font-medium text-gray-800 truncate">
              {goal.title}
            </p>
            <div className="flex justify-between text-xs text-gray-500 mt-1">
              <span>Saved: {fmt(goal.savedAmount)}</span>
              <span>Remaining: {fmt(remaining)}</span>
            </div>

            {/* Mini progress bar */}
            <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden mt-2">
              <div
                className="h-full bg-black rounded-full transition-all duration-300"
                style={{ width: `${Math.min(goal.progressPercentage, 100)}%` }}
              />
            </div>
            <p className="text-xs text-gray-400 mt-1 text-right">
              {goal.progressPercentage.toFixed(1)}% of {fmt(goal.targetAmount)}
            </p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="px-5 pb-5 space-y-4 pt-2">
            {submitError && !submitError.isValidation && (
              <FormError error={submitError.message} />
            )}

            <div>
              <label className="block text-sm text-gray-600 mb-1">
                Amount (₹)
              </label>
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(e) => {
                  setAmount(e.target.value);
                  if (fieldError) setFieldError("");
                }}
                placeholder="e.g. 5000"
                autoFocus
                className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
              />
              <FieldErrorMessage message={fieldError} />

              {/* Quick fill buttons */}
              {remaining > 0 && (
                <div className="flex gap-2 mt-2">
                  {[
                    { label: "25%", val: remaining * 0.25 },
                    { label: "50%", val: remaining * 0.5 },
                    { label: "Full", val: remaining },
                  ].map(({ label, val }) => (
                    <button
                      key={label}
                      type="button"
                      onClick={() => setAmount(val.toFixed(2))}
                      className="text-xs px-2 py-1 border border-gray-200 rounded-lg text-gray-500 hover:bg-gray-50 transition-colors"
                    >
                      {label} ({fmt(val)})
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                disabled={isPending}
                className="flex-1 py-2.5 bg-black text-white text-sm font-medium rounded-lg hover:bg-gray-800 disabled:opacity-60 transition-colors flex items-center justify-center gap-2"
              >
                {isPending && (
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                )}
                {isPending ? "Saving..." : "Contribute"}
              </button>
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2.5 text-sm text-gray-600 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default ContributeModal;
