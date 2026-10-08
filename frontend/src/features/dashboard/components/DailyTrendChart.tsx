import { useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Legend,
} from "recharts";
import type { TooltipContentProps } from "recharts";
import type { DailyTrendItem } from "../dashboard.types";

interface Props {
  data: DailyTrendItem[];
}

type Series = "income" | "expense";

const formatDay = (dateStr: string) => {
  const [year, month, day] = dateStr.split("-").map(Number);
  const d =
    year && month && day
      ? new Date(year, month - 1, day)
      : new Date(dateStr);
  return d.toLocaleDateString("en-IN", { day: "numeric", month: "short" });
};

const formatINR = (value: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(value);

const formatCompactINR = (value: number) =>
  `₹${new Intl.NumberFormat("en-IN", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(value)}`;

const CustomTooltip = ({
  active,
  payload,
  label,
}: TooltipContentProps) => {
  if (!active || !payload?.length) return null;

  return (
    <div className="bg-white border border-gray-200 rounded-lg shadow-sm px-3 py-2 text-xs">
      <p className="font-medium text-gray-700 mb-1">{label}</p>

      {payload.map((entry) => (
        <p
          key={String(entry.dataKey ?? entry.name)}
          style={{ color: entry.color }}
          className="whitespace-nowrap"
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
  const [hoveredSeries, setHoveredSeries] = useState<Series | null>(null);
  const [selectedSeries, setSelectedSeries] = useState<Series | null>(null);
  const activeSeries = hoveredSeries ?? selectedSeries;

  if (!data.length) {
    return (
      <div className="flex items-center justify-center min-h-48 text-sm text-gray-400 text-center px-2">
        No activity this month.
      </div>
    );
  }

  const chartData = data.map((item) => ({
    ...item,
    date: formatDay(item.date),
  }));
  const tickInterval = Math.max(0, Math.ceil(chartData.length / 6) - 1);

  return (
    <div className="w-full min-w-0">
      <div className="mb-2">
        <p className="text-xs text-gray-500">
          Compare money coming in and going out each day.
        </p>
      </div>
      <ResponsiveContainer width="100%" height={280} minWidth={0}>
        <BarChart
          data={chartData}
          margin={{ top: 4, right: 4, left: 0, bottom: 0 }}
          barCategoryGap="25%"
          accessibilityLayer
        >
          <CartesianGrid
            vertical={false}
            stroke="#cbd5e1"
            strokeDasharray="4 4"
            strokeWidth={1}
          />
          <XAxis
            dataKey="date"
            tick={{ fill: "#9ca3af", fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            tickMargin={8}
            interval={tickInterval}
          />
          <YAxis
            tickFormatter={formatCompactINR}
            tick={{ fill: "#9ca3af", fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            width={52}
          />
          <Tooltip content={CustomTooltip} />
          <Legend
            verticalAlign="bottom"
            align="center"
            content={({ payload }) => (
              <div
                aria-label="Chart legend"
                className="flex flex-wrap justify-center gap-x-5 gap-y-2 pt-3 text-xs text-gray-600"
              >
                {payload?.map((entry) => {
                  const series: Series | null =
                    entry.dataKey === "income"
                      ? "income"
                      : entry.dataKey === "expense"
                        ? "expense"
                        : null;

                  if (!series) return null;

                  const label = series === "income" ? "Income" : "Expenses";
                  const color =
                    series === "income" ? "bg-green-500" : "bg-red-500";
                  const focusColor =
                    series === "income"
                      ? "focus-visible:outline-green-600"
                      : "focus-visible:outline-red-600";

                  return (
                    <button
                      key={series}
                      type="button"
                      aria-pressed={selectedSeries === series}
                      onMouseEnter={() => setHoveredSeries(series)}
                      onMouseLeave={() => setHoveredSeries(null)}
                      onFocus={() => setHoveredSeries(series)}
                      onBlur={() => setHoveredSeries(null)}
                      onClick={() =>
                        setSelectedSeries((selected) =>
                          selected === series ? null : series,
                        )
                      }
                      className={`inline-flex items-center gap-1.5 rounded-sm transition-opacity focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 ${focusColor} ${
                        activeSeries && activeSeries !== series
                          ? "opacity-45"
                          : "opacity-100"
                      }`}
                    >
                      <span
                        aria-hidden="true"
                        className={`h-2.5 w-2.5 rounded-sm ${color}`}
                      />
                      {label}
                    </button>
                  );
                })}
              </div>
            )}
          />
          <Bar
            dataKey="income"
            name="Income"
            fill="#22c55e"
            fillOpacity={activeSeries && activeSeries !== "income" ? 0.2 : 1}
            radius={[3, 3, 0, 0]}
            maxBarSize={18}
            animationDuration={200}
          />
          <Bar
            dataKey="expense"
            name="Expenses"
            fill="#ef4444"
            fillOpacity={activeSeries && activeSeries !== "expense" ? 0.2 : 1}
            radius={[3, 3, 0, 0]}
            maxBarSize={18}
            animationDuration={200}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default DailyTrendChart;
