import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import type { TooltipContentProps } from "recharts";
import type { TopExpenseItem } from "../dashboard.types";

interface Props {
  data: TopExpenseItem[];
}

const expenseColors = [
  "#ef4444",
  "#f97316",
  "#fb7185",
  "#dc2626",
  "#fdba74",
];

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

const formatDate = (dateStr: string) => {
  const [year, month, day] = dateStr.split("-").map(Number);
  const date =
    year && month && day
      ? new Date(year, month - 1, day)
      : new Date(dateStr);
  return date.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
  });
};

const ExpenseTooltip = ({ active, payload }: TooltipContentProps) => {
  const item = payload?.[0]?.payload as
    | (TopExpenseItem & { label: string; share: number })
    | undefined;

  if (!active || !item) return null;

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-xs shadow-sm">
      <p className="font-medium text-gray-700">{item.label}</p>
      <p className="mt-1 text-gray-500">
        {fmt(item.amount)}{" "}
        <span className="text-gray-400">({item.share.toFixed(1)}%)</span>
      </p>
    </div>
  );
};

const TopExpenses: React.FC<Props> = ({ data }) => {
  const expenses = data.slice(0, 5);
  const total = expenses.reduce((sum, item) => sum + item.amount, 0);
  const chartData = expenses.map((item) => ({
    ...item,
    label:
      item.note?.trim() ||
      item.categoryName ||
      "Uncategorized expense",
    share: total > 0 ? (item.amount / total) * 100 : 0,
  }));

  if (expenses.length === 0) {
    return (
      <p className="py-4 text-center text-xs text-gray-400">
        No expenses this month.
      </p>
    );
  }

  return (
    <div className="min-w-0">
      <p className="mb-4 text-xs text-gray-400">
        Each slice is one of your five largest expenses.
      </p>
      <div className="flex min-w-0 flex-col items-center gap-4 sm:flex-row sm:items-center sm:gap-6">
        <div
          role="img"
          aria-label={`Top five expenses pie chart, total ${fmt(total)}`}
          className="relative h-[200px] w-full max-w-[230px] shrink-0"
        >
          <ResponsiveContainer width="100%" height="100%" minWidth={0}>
            <PieChart>
              <Pie
                data={chartData}
                dataKey="amount"
                nameKey="label"
                cx="50%"
                cy="50%"
                innerRadius="58%"
                outerRadius="82%"
                paddingAngle={3}
                stroke="#ffffff"
                strokeWidth={2}
                isAnimationActive={false}
              >
                {chartData.map((item, index) => (
                  <Cell key={item.id} fill={expenseColors[index]} />
                ))}
              </Pie>
              <Tooltip content={ExpenseTooltip} />
            </PieChart>
          </ResponsiveContainer>
          <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-[10px] uppercase tracking-wide text-gray-400">
              Top 5 total
            </span>
            <span className="max-w-[120px] truncate text-sm font-semibold text-gray-800">
              {fmt(total)}
            </span>
          </div>
        </div>

        <ol className="w-full min-w-0 space-y-3">
          {chartData.map((item, index) => (
            <li
              key={item.id}
              className="flex min-w-0 items-start gap-2.5 text-xs"
            >
              <span
                aria-hidden="true"
                className="mt-0.5 h-2.5 w-2.5 shrink-0 rounded-full"
                style={{ backgroundColor: expenseColors[index] }}
              />
              <div className="min-w-0 flex-1">
                <div className="flex min-w-0 items-baseline justify-between gap-2">
                  <span className="min-w-0 truncate font-medium text-gray-700">
                    {item.categoryName ?? "Uncategorized"}
                  </span>
                  <span className="shrink-0 text-gray-400">
                    {item.share.toFixed(0)}%
                  </span>
                </div>
                <div className="mt-0.5 flex min-w-0 items-center justify-between gap-2">
                  <span className="min-w-0 truncate text-gray-400">
                    {item.note?.trim() || formatDate(item.date)}
                  </span>
                  <span className="shrink-0 font-medium text-red-600">
                    {fmt(item.amount)}
                  </span>
                </div>
              </div>
            </li>
          ))}
        </ol>
      </div>
    </div>
  );
};

export default TopExpenses;
