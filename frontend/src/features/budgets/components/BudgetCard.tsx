import { useState } from "react";
import { budgetService } from "../budgetService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import Spinner from "../../../shared/components/ui/Spinner";
import type { BudgetResponse } from "../budget.types";

interface Props {
  budget: BudgetResponse;
  onDelete: (id: string) => void;
  isDeleting: boolean;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const BudgetCard: React.FC<Props> = ({ budget, onDelete, isDeleting }) => {
  const [expanded, setExpanded] = useState(false);

  // Only fetch status when card is expanded — avoids N queries on load
  const { data, isLoading } = useAppQuery({
    queryKey: ["budget-status", budget.id],
    queryFn: () => budgetService.getStatus(budget.id),
    enabled: expanded,
  });

  const status = data?.data;

  // Determine bar color from status
  const getBarColor = (limitBreached: boolean, thresholdBreached: boolean) => {
    if (limitBreached) return "bg-red-500";
    if (thresholdBreached) return "bg-amber-400";
    return "bg-green-500";
  };

  const getStatusBadge = () => {
    if (!status) return null;
    if (status.limitBreached) {
      return (
        <span className="text-xs font-medium text-red-600 bg-red-50 px-2 py-0.5 rounded-full">
          Over limit
        </span>
      );
    }
    if (status.thresholdBreached) {
      return (
        <span className="text-xs font-medium text-amber-600 bg-amber-50 px-2 py-0.5 rounded-full">
          Near limit
        </span>
      );
    }
    return (
      <span className="text-xs font-medium text-green-600 bg-green-50 px-2 py-0.5 rounded-full">
        On track
      </span>
    );
  };

  return (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
      {/* ── Summary row ── */}
      <div className="flex items-center justify-between px-4 py-4">
        <div className="flex items-center gap-3 min-w-0">
          {/* Expand toggle */}
          <button
            onClick={() => setExpanded((v) => !v)}
            className="text-gray-400 hover:text-gray-700 transition-colors text-xs w-4 shrink-0"
            aria-label={expanded ? "Collapse" : "Expand"}
          >
            {expanded ? "▼" : "▶"}
          </button>

          {/* Category */}
          <div className="min-w-0">
            <p className="text-sm font-medium text-gray-800 truncate">
              {budget.categoryName}
            </p>
            <p className="text-xs text-gray-400 mt-0.5">
              Alert at {budget.alertThreshold}%
            </p>
          </div>
        </div>

        <div className="flex items-center gap-4 shrink-0">
          {/* Status badge — only shown when expanded and loaded */}
          {expanded && status && getStatusBadge()}

          {/* Limit */}
          <div className="text-right">
            <p className="text-sm font-semibold text-gray-800">
              {fmt(budget.limitAmount)}
            </p>
            <p className="text-xs text-gray-400">limit</p>
          </div>

          {/* Delete */}
          <button
            onClick={() => onDelete(budget.id)}
            disabled={isDeleting}
            className="text-xs text-red-500 hover:text-red-700 disabled:opacity-40 transition-colors ml-2"
          >
            Delete
          </button>
        </div>
      </div>

      {/* ── Expanded status ── */}
      {expanded && (
        <div className="border-t border-gray-100 px-4 py-4 bg-gray-50">
          {isLoading ? (
            <div className="flex justify-center py-4">
              <Spinner />
            </div>
          ) : status ? (
            <div className="space-y-4">
              {/* Progress bar */}
              <div>
                <div className="flex justify-between text-xs text-gray-500 mb-1.5">
                  <span>
                    Spent{" "}
                    <span className="font-medium text-gray-700">
                      {fmt(status.spentAmount)}
                    </span>
                  </span>
                  <span>{status.usagePercentage.toFixed(1)}% used</span>
                </div>
                <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all duration-500 ${getBarColor(
                      status.limitBreached,
                      status.thresholdBreached,
                    )}`}
                    style={{
                      width: `${Math.min(status.usagePercentage, 100)}%`,
                    }}
                  />
                </div>
              </div>

              {/* Stats row */}
              <div className="grid grid-cols-3 gap-3 text-center">
                <div className="bg-white border border-gray-200 rounded-lg py-2 px-3">
                  <p className="text-xs text-gray-400 mb-0.5">Spent</p>
                  <p className="text-sm font-semibold text-red-600">
                    {fmt(status.spentAmount)}
                  </p>
                </div>
                <div className="bg-white border border-gray-200 rounded-lg py-2 px-3">
                  <p className="text-xs text-gray-400 mb-0.5">Remaining</p>
                  <p
                    className={`text-sm font-semibold ${status.limitBreached ? "text-red-600" : "text-green-600"}`}
                  >
                    {status.limitBreached
                      ? `-${fmt(Math.abs(status.remainingAmount))}`
                      : fmt(status.remainingAmount)}
                  </p>
                </div>
                <div className="bg-white border border-gray-200 rounded-lg py-2 px-3">
                  <p className="text-xs text-gray-400 mb-0.5">Limit</p>
                  <p className="text-sm font-semibold text-gray-700">
                    {fmt(status.limitAmount)}
                  </p>
                </div>
              </div>

              {/* Warning messages */}
              {status.limitBreached && (
                <p className="text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
                  ⚠ You have exceeded your budget limit for this category.
                </p>
              )}
              {!status.limitBreached && status.thresholdBreached && (
                <p className="text-xs text-amber-700 bg-amber-50 border border-amber-100 rounded-lg px-3 py-2">
                  ⚡ You are approaching your budget limit (
                  {budget.alertThreshold}% threshold reached).
                </p>
              )}
            </div>
          ) : (
            <p className="text-xs text-gray-400 text-center py-2">
              Unable to load status.
            </p>
          )}
        </div>
      )}
    </div>
  );
};

export default BudgetCard;
