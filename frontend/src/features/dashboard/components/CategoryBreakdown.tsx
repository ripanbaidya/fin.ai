import {
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import type { TooltipContentProps } from "recharts";
import type { CategoryBreakdownResponse } from "../dashboard.types";

interface Props {
  title: string;
  data: CategoryBreakdownResponse[];
  type: "income" | "expense";
}

const incomeColors = [
  "#16a34a",
  "#22c55e",
  "#4ade80",
  "#15803d",
  "#86efac",
  "#65a30d",
  "#a3e635",
  "#047857",
];

const expenseColors = [
  "#ef4444",
  "#f97316",
  "#fb7185",
  "#dc2626",
  "#fdba74",
  "#e11d48",
  "#fda4af",
  "#c2410c",
];

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const CategoryTooltip = ({
  active,
  payload,
}: TooltipContentProps) => {
  const item = payload?.[0]?.payload as
    | CategoryBreakdownResponse
    | undefined;

  if (!active || !item) return null;

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-3 py-2 text-xs shadow-sm">
      <p className="font-medium text-gray-700">{item.categoryName}</p>
      <p className="mt-1 text-gray-500">
        {fmt(item.amount)} <span className="text-gray-400">({item.percentage.toFixed(1)}%)</span>
      </p>
    </div>
  );
};

const CategoryBreakdown: React.FC<Props> = ({ title, data, type }) => {
  const colors = type === "income" ? incomeColors : expenseColors;
  const total = data.reduce((sum, item) => sum + item.amount, 0);
  const chartData = data.filter((item) => item.amount > 0);

  if (data.length === 0) {
    return (
      <div>
        <h3 className="text-sm font-medium text-gray-700 mb-3">{title}</h3>
        <p className="text-xs text-gray-400 py-4 text-center">
          No data this month.
        </p>
      </div>
    );
  }

  return (
    <div className="min-w-0">
      <h3 className="text-sm font-medium text-gray-700 mb-1">{title}</h3>
      <p className="text-xs text-gray-400 mb-4">
        How your {type === "income" ? "income" : "spending"} is split by category
      </p>

      <div className="flex min-w-0 flex-col items-center gap-4 sm:flex-row sm:items-center sm:gap-3">
        <div
          role="img"
          aria-label={`${title} pie chart, total ${fmt(total)}`}
          className="relative h-[190px] w-full max-w-[220px] shrink-0"
        >
          <ResponsiveContainer width="100%" height="100%" minWidth={0}>
            <PieChart>
              <Pie
                data={chartData}
                dataKey="amount"
                nameKey="categoryName"
                cx="50%"
                cy="50%"
                innerRadius="58%"
                outerRadius="82%"
                paddingAngle={2}
                stroke="#ffffff"
                strokeWidth={2}
                isAnimationActive={false}
              >
                {chartData.map((item) => {
                  const colorIndex = data.indexOf(item) % colors.length;
                  return (
                    <Cell
                      key={item.categoryName}
                      fill={colors[colorIndex]}
                    />
                  );
                })}
              </Pie>
              <Tooltip content={CategoryTooltip} />
            </PieChart>
          </ResponsiveContainer>
          <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-[10px] uppercase tracking-wide text-gray-400">
              Total
            </span>
            <span className="max-w-[100px] truncate text-sm font-semibold text-gray-800">
              {fmt(total)}
            </span>
          </div>
        </div>

        <ul className="w-full min-w-0 space-y-2">
          {data.map((item, index) => (
            <li
              key={item.categoryName}
              className="flex min-w-0 items-center gap-2 text-xs"
              title={item.categoryName}
            >
              <span
                aria-hidden="true"
                className="h-2.5 w-2.5 shrink-0 rounded-full"
                style={{ backgroundColor: colors[index % colors.length] }}
              />
              <span className="min-w-0 flex-1 truncate text-gray-600">
                {item.categoryName}
              </span>
              <span className="shrink-0 text-right">
                <span className="font-medium text-gray-700">
                  {fmt(item.amount)}
                </span>
                <span className="ml-1 text-gray-400">
                  {item.percentage.toFixed(0)}%
                </span>
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default CategoryBreakdown;
