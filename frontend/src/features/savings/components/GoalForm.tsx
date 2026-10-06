import { useState } from "react";
import { AppError } from "../../../api/errorParser";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";
import type { CreateSavingsGoalRequest } from "../savings.types";

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: (data: CreateSavingsGoalRequest) => void;
  isPending: boolean;
  submitError: AppError | null;
}

interface FormState {
  title: string;
  targetAmount: string;
  deadline: string;
  note: string;
}

// Tomorrow as minimum deadline
const tomorrow = () => {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
};

const EMPTY: FormState = {
  title: "",
  targetAmount: "",
  deadline: "",
  note: "",
};

const GoalForm: React.FC<Props> = ({
  open,
  onClose,
  onSubmit,
  isPending,
  submitError,
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
    if (!form.title.trim()) errors.title = "Title is required";
    const amt = Number(form.targetAmount);
    if (!form.targetAmount || isNaN(amt) || amt <= 0) {
      errors.targetAmount = "Enter a valid target amount";
    }
    if (!form.deadline) {
      errors.deadline = "Deadline is required";
    } else if (form.deadline <= new Date().toISOString().split("T")[0]) {
      errors.deadline = "Deadline must be a future date";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!validate()) return;
    onSubmit({
      title: form.title.trim(),
      targetAmount: Number(form.targetAmount),
      deadline: form.deadline,
      note: form.note.trim() || undefined,
    });
  };

  if (!open) return null;

  return (
    <>
      <div className="fixed inset-0 bg-black/30 z-30" onClick={onClose} />

      <div className="fixed inset-y-0 right-0 w-full max-w-md bg-white shadow-xl z-40 flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
          <h2 className="text-base font-semibold text-gray-900">
            New Savings Goal
          </h2>
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

          {/* Title */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Goal Title
            </label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => set("title", e.target.value)}
              placeholder="e.g. Buy MacBook Pro"
              autoFocus
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={errors.title} />
          </div>

          {/* Target amount */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Target Amount (₹)
            </label>
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={form.targetAmount}
              onChange={(e) => set("targetAmount", e.target.value)}
              placeholder="e.g. 150000"
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={errors.targetAmount} />
          </div>

          {/* Deadline */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">Deadline</label>
            <input
              type="date"
              value={form.deadline}
              min={tomorrow()}
              onChange={(e) => set("deadline", e.target.value)}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
            />
            <FieldErrorMessage message={errors.deadline} />
          </div>

          {/* Note */}
          <div>
            <label className="block text-sm text-gray-600 mb-1">
              Note <span className="text-gray-400 font-normal">(optional)</span>
            </label>
            <textarea
              value={form.note}
              onChange={(e) => set("note", e.target.value)}
              placeholder="e.g. Saving ₹10k monthly"
              rows={3}
              maxLength={500}
              className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black resize-none"
            />
          </div>
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
            {isPending ? "Creating..." : "Create Goal"}
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

export default GoalForm;
