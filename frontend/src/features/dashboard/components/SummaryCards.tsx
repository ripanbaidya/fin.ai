import type { DashboardSummary } from "../dashboard.types";

interface Props {
  summary: DashboardSummary;
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);

const SummaryCards: React.FC<Props> = ({ summary }) => {
  const { totalIncome, totalExpense, netBalance } = summary;

  // Calculate savings rate safely
  const savingsRate =
    totalIncome > 0 ? Math.max(0, (netBalance / totalIncome) * 100) : 0;

  const cards = [
    {
      label: "Total Income",
      value: fmt(totalIncome),
      color: "text-green-600",
      bg: "bg-green-50",
      border: "border-green-100",
      icon: "↑",
    },
    {
      label: "Total Expense",
      value: fmt(totalExpense),
      color: "text-red-600",
      bg: "bg-red-50",
      border: "border-red-100",
      icon: "↓",
    },
    {
      label: "Net Balance",
      value: fmt(netBalance),
      color: netBalance >= 0 ? "text-gray-900" : "text-red-600",
      bg: "bg-white",
      border: "border-gray-200",
      icon: "=",
    },
    {
      label: "Savings Rate",
      value: `${savingsRate.toFixed(1)}%`,
      color:
        savingsRate >= 20
          ? "text-green-600"
          : savingsRate >= 10
            ? "text-amber-600"
            : "text-red-500",
      bg: "bg-white",
      border: "border-gray-200",
      icon: "◎",
    },
  ];

  return (
    // Responsive grid:
    // 2 cols on mobile → 3 on tablet → 4 on desktop
    <div className="grid grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      {cards.map((card) => (
        <div
          key={card.label}
          className={`${card.bg} border ${card.border} rounded-lg p-4 min-w-0`}
        >
          {/* Top row: label + icon */}
          <div className="flex items-center justify-between mb-2 gap-2">
            <p className="text-xs text-gray-500 font-medium truncate">
              {card.label}
            </p>

            {/* Prevent icon shrinking */}
            <span
              className={`text-sm font-semibold ${card.color} flex-shrink-0`}
            >
              {card.icon}
            </span>
          </div>

          {/* Value */}
          <p
            className={`
              text-lg sm:text-xl font-semibold ${card.color}
              truncate        /* prevents overflow on small screens */
            `}
          >
            {card.value}
          </p>
        </div>
      ))}
    </div>
  );
};

export default SummaryCards;
