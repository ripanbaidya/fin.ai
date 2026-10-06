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
              onClick={() => onEdit(rule)}
              className="text-xs text-gray-500 hover:text-gray-900 border border-gray-200 px-2.5 py-1 rounded-lg hover:bg-gray-50 transition-colors whitespace-nowrap"
            >
              Edit
            </button>

            <button
              onClick={() => onDeactivate(rule.id)}
              disabled={isDeactivating}
              className="text-xs text-red-500 hover:text-red-700 border border-red-100 px-2.5 py-1 rounded-lg hover:bg-red-50 transition-colors disabled:opacity-40 whitespace-nowrap"
            >
              Stop
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RecurringCard;
