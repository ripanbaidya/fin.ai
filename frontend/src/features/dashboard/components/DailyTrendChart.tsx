import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  Legend,
  CartesianGrid,
} from "recharts";
import type { TooltipContentProps } from "recharts";
import type { DailyTrendItem } from "../dashboard.types";

interface Props {
  data: DailyTrendItem[];
}

// Format date → "12 Mar"
const formatDay = (dateStr: string) => {
  const d = new Date(dateStr);
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
};

// Format currency → ₹1,000
const formatINR = (value: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(value);

// Tooltip (unchanged)
const CustomTooltip = ({
  active,
  payload,
  label,
}: TooltipContentProps) => {
  if (!active || !payload?.length) return null;

  return (
    <div className="bg-white border border-gray-200 rounded-lg shadow-sm px-3 py-2 text-xs max-w-[200px]">
      <p className="font-medium text-gray-700 mb-1 truncate">{label}</p>

      {payload.map((entry) => (
        <p
          key={String(entry.dataKey ?? entry.name)}
          style={{ color: entry.color }}
          className="truncate"
        >
          {entry.name}:{" "}
          {typeof entry.value === "number"
            ? formatINR(entry.value)
            : entry.value}
        </p>
      ))}
    </div>
  );
};

const DailyTrendChart: React.FC<Props> = ({ data }) => {
  if (!data.length) {
    return (
      <div className="flex items-center justify-center h-48 text-sm text-gray-400 text-center px-2">
        No activity this month.
      </div>
    );
  }

  const chartData = data.map((item) => ({
    ...item,
    date: formatDay(item.date),
  }));

  return (
    <ResponsiveContainer width="100%" height={240}>
      <BarChart data={chartData}>
        {/* Grid */}
        <CartesianGrid stroke="#f1f5f9" strokeDasharray="3 3" />

        {/* X-axis */}
        <XAxis
          dataKey="date"
          tick={{ fontSize: 11 }}
          interval="preserveStartEnd"
        />

        {/* Y-axis */}
        <YAxis tickFormatter={(v) => `₹${v}`} width={40} />

        {/* Tooltip */}
        <Tooltip content={CustomTooltip} />

        {/* Legend */}
        <Legend wrapperStyle={{ fontSize: "12px" }} />

        {/* Bars */}
        <Bar
          dataKey="income"
          fill="#22c55e"
          radius={[4, 4, 0, 0]}
          barSize={12}
        />

        <Bar
          dataKey="expense"
          fill="#ef4444"
          radius={[4, 4, 0, 0]}
          barSize={12}
        />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default DailyTrendChart;
