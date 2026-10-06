import type { TxnType } from "../transaction.types";
import type { FilterState } from "../transactionFilters.types";
import MonthNavigator from "../../../shared/components/layout/MonthNavigator";

interface Props {
  filters: FilterState;
  month: string;
  onMonthChange: (month: string) => void;
  onChange: (filters: FilterState) => void;
  onReset: () => void;
}

const TransactionFilters: React.FC<Props> = ({
  filters,
  month,
  onMonthChange,
  onChange,
  onReset,
}) => {
  const hasActiveFilters =
    filters.type !== "" || filters.dateFrom !== "" || filters.dateTo !== "";

  return (
    <div className="flex flex-wrap items-end gap-3 bg-white border border-gray-200 rounded-lg p-4">
      <div className="w-full flex items-center justify-between gap-3 pb-1 border-b border-gray-100 mb-1">
        <p className="text-xs text-gray-500 font-medium">Month</p>
        <MonthNavigator month={month} onChange={onMonthChange} />
      </div>

      {/* Type */}
      <div className="flex flex-col gap-1 min-w-[120px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">Type</label>

        <select
          value={filters.type}
          onChange={(e) =>
            onChange({ ...filters, type: e.target.value as TxnType | "" })
          }
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black bg-white
            w-full sm:min-w-[130px]   /* full width on mobile */
          "
        >
          <option value="">All types</option>
          <option value="INCOME">Income</option>
          <option value="EXPENSE">Expense</option>
        </select>
      </div>

      {/* Date From */}
      <div className="flex flex-col gap-1 min-w-[140px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">From</label>

        <input
          type="date"
          value={filters.dateFrom}
          onChange={(e) => onChange({ ...filters, dateFrom: e.target.value })}
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black
            w-full
          "
        />
      </div>

      {/* Date To */}
      <div className="flex flex-col gap-1 min-w-[140px] flex-1 sm:flex-none">
        <label className="text-xs text-gray-500 font-medium">To</label>

        <input
          type="date"
          value={filters.dateTo}
          onChange={(e) => onChange({ ...filters, dateTo: e.target.value })}
          className="
            px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none
            focus:ring-2 focus:ring-black
            w-full
          "
        />
      </div>

      {/* Reset */}
      {hasActiveFilters && (
        <button
          onClick={onReset}
          className="
            px-3 py-2 text-sm text-gray-500 border border-gray-200 rounded-lg
            hover:bg-gray-50 transition-colors
            w-full sm:w-auto        /* full width on mobile */
            flex-shrink-0
          "
        >
          Reset
        </button>
      )}
    </div>
  );
};

export default TransactionFilters;
