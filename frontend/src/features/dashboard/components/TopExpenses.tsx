import type { TopExpenseItem } from "../dashboard.types";

interface Props {
  data: TopExpenseItem[];
}

const fmt = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

// Format → "12 Mar"
const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
  });

const TopExpenses: React.FC<Props> = ({ data }) => {
  if (data.length === 0) {
    return (
      <p className="text-xs text-gray-400 py-4 text-center">
        No expenses this month.
      </p>
    );
  }

  return (
    // Wrapper enables horizontal scroll on small screens
    <div className="w-full overflow-x-auto">
      <table className="w-full min-w-[600px]">
        {/* min-w prevents table from collapsing too much */}

        <thead>
          <tr className="text-left text-xs text-gray-400 uppercase tracking-wide border-b border-gray-100">
            <th className="pb-2 font-medium whitespace-nowrap">#</th>

            <th className="pb-2 font-medium whitespace-nowrap">Category</th>

            {/* Hide Note column on very small screens */}
            <th className="pb-2 font-medium hidden sm:table-cell">Note</th>

            <th className="pb-2 font-medium whitespace-nowrap">Date</th>

            <th className="pb-2 font-medium text-right whitespace-nowrap">
              Amount
            </th>
          </tr>
        </thead>

        <tbody>
          {data.map((item, idx) => (
            <tr
              key={item.id}
              className="border-t border-gray-50 hover:bg-gray-50 transition-colors"
            >
              {/* Index */}
              <td className="py-2.5 pr-3 text-xs text-gray-400 whitespace-nowrap">
                {idx + 1}
              </td>

              {/* Category */}
              <td className="py-2.5 pr-3 text-sm text-gray-700 truncate max-w-[140px]">
                {item.categoryName ?? <span className="text-gray-400">—</span>}
              </td>

              {/* Note (hidden on mobile for better fit) */}
              <td className="py-2.5 pr-3 text-sm text-gray-500 max-w-[160px] truncate hidden sm:table-cell">
                {item.note ?? <span className="text-gray-300">—</span>}
              </td>

              {/* Date */}
              <td className="py-2.5 pr-3 text-xs text-gray-400 whitespace-nowrap">
                {formatDate(item.date)}
              </td>

              {/* Amount */}
              <td className="py-2.5 text-sm font-medium text-red-600 text-right whitespace-nowrap">
                -{fmt(item.amount)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TopExpenses;
