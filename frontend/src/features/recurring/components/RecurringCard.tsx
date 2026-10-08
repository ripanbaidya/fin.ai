import { FiEdit2 } from "react-icons/fi";
import { RiStopCircleLine } from "react-icons/ri";
import type {
  RecurringTransactionResponse,
  RecurringFrequency,
} from "../recurring.types";

interface Props {
  rule: RecurringTransactionResponse;
  onEdit: (rule: RecurringTransactionResponse) => void;
  onDeactivate: (id: string) => void;
  isDeactivating: boolean;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });

const FREQ_BADGE: Record<RecurringFrequency, { label: string; color: string }> =
  {
    DAILY: { label: "Daily", color: "bg-purple-50 text-purple-700" },
    WEEKLY: { label: "Weekly", color: "bg-blue-50 text-blue-700" },
    MONTHLY: { label: "Monthly", color: "bg-indigo-50 text-indigo-700" },
    YEARLY: { label: "Yearly", color: "bg-orange-50 text-orange-700" },
  };

const RecurringCard: React.FC<Props> = ({
  rule,
  onEdit,
  onDeactivate,
  isDeactivating,
}) => {
  const freq = FREQ_BADGE[rule.frequency];
  const isIncome = rule.type === "INCOME";

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4 hover:border-gray-300 transition-colors">
      {/* Stack on mobile, row on larger screens */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
        {/* LEFT */}
        <div className="min-w-0 flex-1">
          {/* Title + badges */}
          <div className="flex items-center gap-2 flex-wrap">
            <p className="text-sm font-semibold text-gray-900 truncate max-w-full">
              {rule.title}
            </p>

            <span
              className={`text-xs font-medium px-2 py-0.5 rounded-full whitespace-nowrap ${freq.color}`}
            >
              {freq.label}
            </span>

            <span
              className={`text-xs font-medium px-2 py-0.5 rounded-full whitespace-nowrap ${
                isIncome
                  ? "bg-green-50 text-green-700"
                  : "bg-red-50 text-red-600"
              }`}
            >
              {isIncome ? "↑ Income" : "↓ Expense"}
            </span>
          </div>

          {/* Meta row */}
          <div className="flex flex-wrap items-center gap-x-3 gap-y-1 mt-2 text-xs text-gray-400 min-w-0">
            {rule.categoryName && (
              <span className="truncate max-w-[120px]">
                {rule.categoryName}
              </span>
            )}

            {rule.paymentModeName && (
              <>
                {rule.categoryName && <span>·</span>}
                <span className="truncate max-w-[120px]">
                  {rule.paymentModeName}
                </span>
              </>
            )}

            {rule.note && (
              <>
                <span>·</span>
                <span className="truncate max-w-[160px]">{rule.note}</span>
              </>
            )}
          </div>

          {/* Dates */}
          <div className="flex flex-wrap items-center gap-x-3 mt-2 text-xs text-gray-400">
            <span>
              Started{" "}
              <span className="text-gray-600">
                {formatDate(rule.startDate)}
              </span>
            </span>

            {rule.endDate && (
              <span>
                · Ends{" "}
                <span className="text-gray-600">
                  {formatDate(rule.endDate)}
                </span>
              </span>
            )}

            <span>
              · Next{" "}
              <span className="text-gray-600 font-medium">
                {formatDate(rule.nextExecutionDate)}
              </span>
            </span>
          </div>
        </div>

        {/* RIGHT */}
        <div className="flex flex-row sm:flex-col items-center sm:items-end justify-between sm:justify-start gap-3 w-full sm:w-auto">
          {/* Amount */}
          <p
            className={`text-base font-semibold whitespace-nowrap ${
              isIncome ? "text-green-600" : "text-red-600"
            }`}
          >
            {isIncome ? "+" : "-"}
            {fmt(rule.amount)}
          </p>

          {/* Actions */}
          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => onEdit(rule)}
              aria-label="Edit recurring transaction"
              title="Edit recurring transaction"
              className="inline-flex h-10 w-10 items-center justify-center rounded-full text-gray-500 transition-colors hover:text-gray-900 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600"
            >
              <FiEdit2 aria-hidden="true" size={16} />
            </button>

            <button
              type="button"
              onClick={() => onDeactivate(rule.id)}
              disabled={isDeactivating}
              aria-label="Stop recurring transaction"
              title="Stop recurring transaction"
              className="inline-flex h-10 w-10 items-center justify-center rounded-full text-red-500 transition-colors hover:text-red-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <RiStopCircleLine aria-hidden="true" size={18} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecurringCard;
