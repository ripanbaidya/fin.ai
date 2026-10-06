import type { CategoryBreakdownResponse } from "../dashboard.types";

interface Props {
  title: string;
  data: CategoryBreakdownResponse[];
  type: "income" | "expense";
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const CategoryBreakdown: React.FC<Props> = ({ title, data, type }) => {
  const barColor = type === "income" ? "bg-green-500" : "bg-red-500";
  const barBg = type === "income" ? "bg-green-50" : "bg-red-50";

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
    <div>
      <h3 className="text-sm font-medium text-gray-700 mb-3">{title}</h3>

      <div className="space-y-3">
        {data.map((item) => (
          <div key={item.categoryName} className="min-w-0">
            {/* Label row */}
            <div className="flex items-center justify-between gap-2 text-xs mb-1">
              {/* LEFT: Category */}
              <span className="text-gray-600 truncate flex-1 min-w-0">
                {item.categoryName}
              </span>

              {/* RIGHT: Amount + % */}
              <span className="text-gray-700 font-medium whitespace-nowrap flex-shrink-0">
                {fmt(item.amount)}{" "}
                <span className="text-gray-400 font-normal">
                  ({item.percentage.toFixed(1)}%)
                </span>
              </span>
            </div>

            {/* Progress bar */}
            <div className={`h-1.5 rounded-full ${barBg} overflow-hidden`}>
              <div
                className={`h-full rounded-full ${barColor} transition-all duration-500`}
                style={{
                  width: `${Math.min(item.percentage, 100)}%`,
                }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryBreakdown;
