import { RiDeleteBinLine } from "react-icons/ri";
import type { GoalProgressResponse, GoalStatus } from "../savings.types";

interface Props {
  goal: GoalProgressResponse;
  onContribute: (goal: GoalProgressResponse) => void;
  onDelete: (id: string) => void;
  isDeleting: boolean;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });

const STATUS_STYLES: Record<GoalStatus, { badge: string; bar: string }> = {
  IN_PROGRESS: { badge: "bg-blue-50 text-blue-600", bar: "bg-blue-500" },
  ACHIEVED: { badge: "bg-green-50 text-green-600", bar: "bg-green-500" },
  FAILED: { badge: "bg-red-50 text-red-500", bar: "bg-red-400" },
};

const STATUS_LABEL: Record<GoalStatus, string> = {
  IN_PROGRESS: "In Progress",
  ACHIEVED: "Achieved ✓",
  FAILED: "Failed",
};

const GoalCard: React.FC<Props> = ({ goal, onContribute, onDelete, isDeleting }) => {
  const style = STATUS_STYLES[goal.status];
  const isActive = goal.status === "IN_PROGRESS";

  const daysLabel = () => {
    if (goal.status === "ACHIEVED") return null;
    if (goal.daysRemaining < 0) {
      return (
        <span className="text-xs text-red-500">
          {Math.abs(goal.daysRemaining)}d overdue
        </span>
      );
    }
    return (
      <span className="text-xs text-gray-400">
        {goal.daysRemaining}d remaining
      </span>
    );
  };

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-5 space-y-4">
      {/* ── Top row ── */}
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-gray-900 truncate">
            {goal.title}
          </p>
          <div className="flex flex-wrap items-center gap-x-2 gap-y-1 mt-0.5">
            <span className="text-xs text-gray-400">
              Deadline: {formatDate(goal.deadline)}
            </span>
            {daysLabel()}
          </div>
        </div>
        <span
          className={`text-xs font-medium px-2 py-0.5 rounded-full shrink-0 ${style.badge}`}
        >
          {STATUS_LABEL[goal.status]}
        </span>
      </div>

      {/* ── Progress bar ── */}
      <div>
        <div className="flex flex-wrap justify-between gap-x-2 gap-y-1 text-xs text-gray-500 mb-1.5">
          <span>
            Saved{" "}
            <span className="font-medium text-gray-700">
              {fmt(goal.savedAmount)}
            </span>
          </span>
          <span className="font-medium text-gray-700">
            {goal.progressPercentage.toFixed(1)}%
          </span>
        </div>
        <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-500 ${style.bar}`}
            style={{ width: `${Math.min(goal.progressPercentage, 100)}%` }}
          />
        </div>
      </div>

      {/* ── Stats row ── */}
      <div className="grid grid-cols-1 gap-2 text-center min-[480px]:grid-cols-3 min-[480px]:gap-3">
        <div className="bg-gray-50 rounded-lg py-2 px-3">
          <p className="text-xs text-gray-400 mb-0.5">Target</p>
          <p className="text-sm font-semibold text-gray-700">
            {fmt(goal.targetAmount)}
          </p>
        </div>
        <div className="bg-gray-50 rounded-lg py-2 px-3">
          <p className="text-xs text-gray-400 mb-0.5">Saved</p>
          <p className="text-sm font-semibold text-green-600">
            {fmt(goal.savedAmount)}
          </p>
        </div>
        <div className="bg-gray-50 rounded-lg py-2 px-3">
          <p className="text-xs text-gray-400 mb-0.5">Remaining</p>
          <p
            className={`text-sm font-semibold ${goal.status === "ACHIEVED" ? "text-green-600" : "text-gray-700"}`}
          >
            {goal.status === "ACHIEVED" ? "—" : fmt(goal.remainingAmount)}
          </p>
        </div>
      </div>

      {/* ── Contribute button ── */}
      <div className="flex items-center gap-2">
        {isActive && (
          <button
            onClick={() => onContribute(goal)}
            className="flex-1 py-2 text-sm font-medium border border-gray-200 rounded-lg text-gray-700 hover:bg-gray-50 hover:border-gray-300 transition-colors"
          >
            + Add Contribution
          </button>
        )}
        <button
          type="button"
          onClick={() => onDelete(goal.id)}
          disabled={isDeleting}
          aria-label="Delete savings goal"
          title="Delete savings goal"
          className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-red-500 transition-colors hover:text-red-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <RiDeleteBinLine aria-hidden="true" size={18} />
        </button>
      </div>
    </div>
  );
};

export default GoalCard;
