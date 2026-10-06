import type { PaginationMeta } from "../../../types/api.types";
import type { TransactionResponse } from "../transaction.types";

interface Props {
  transactions: TransactionResponse[];
  pageInfo: PaginationMeta;
  currentPage: number;
  onPageChange: (page: number) => void;
  onEdit: (txn: TransactionResponse) => void;
  onDelete: (id: string) => void;
  isDeleting: boolean;
}

const formatAmount = (amount: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(amount);

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });

const TransactionTable: React.FC<Props> = ({
  transactions,
  pageInfo,
  currentPage,
  onPageChange,
  onEdit,
  onDelete,
  isDeleting,
}) => {
  const totalPages = pageInfo.totalPages;
  const start = currentPage * pageInfo.size + 1;
  const end = Math.min(start + pageInfo.size - 1, pageInfo.totalElements);

  return (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
      {/* Horizontal scroll wrapper (CRITICAL for responsiveness) */}
      <div className="w-full overflow-x-auto">
        <table className="w-full min-w-[800px]">
          {/* min-w ensures table doesn't collapse */}

          <thead>
            <tr className="text-left text-xs text-gray-400 uppercase tracking-wide bg-gray-50 border-b border-gray-100">
              <th className="px-4 py-3 font-medium whitespace-nowrap">Date</th>
              <th className="px-4 py-3 font-medium whitespace-nowrap">Type</th>
              <th className="px-4 py-3 font-medium whitespace-nowrap">
                Category
              </th>

              {/* Hide less important columns on small screens */}
              <th className="px-4 py-3 font-medium whitespace-nowrap hidden sm:table-cell">
                Payment Mode
              </th>

              <th className="px-4 py-3 font-medium hidden md:table-cell">
                Note
              </th>

              <th className="px-4 py-3 font-medium text-right whitespace-nowrap">
                Amount
              </th>

              <th className="px-4 py-3 font-medium text-right whitespace-nowrap">
                Actions
              </th>
            </tr>
          </thead>

          <tbody>
            {transactions.map((txn) => (
              <tr
                key={txn.id}
                className="border-t border-gray-100 hover:bg-gray-50 transition-colors"
              >
                {/* Date */}
                <td className="px-4 py-3 text-sm text-gray-600 whitespace-nowrap">
                  {formatDate(txn.date)}
                </td>

                {/* Type */}
                <td className="px-4 py-3">
                  <span
                    className={`inline-block text-xs font-medium px-2 py-0.5 rounded-full ${
                      txn.type === "INCOME"
                        ? "bg-green-50 text-green-700"
                        : "bg-red-50 text-red-600"
                    }`}
                  >
                    {txn.type}
                  </span>
                </td>

                {/* Category */}
                <td className="px-4 py-3 text-sm text-gray-700 truncate max-w-[140px]">
                  {txn.categoryName ?? <span className="text-gray-400">—</span>}
                </td>

                {/* Payment Mode (hidden on small screens) */}
                <td className="px-4 py-3 text-sm text-gray-700 hidden sm:table-cell whitespace-nowrap">
                  {txn.paymentModeName ?? (
                    <span className="text-gray-400">—</span>
                  )}
                </td>

                {/* Note (hidden on smaller screens) */}
                <td className="px-4 py-3 text-sm text-gray-500 max-w-[180px] truncate hidden md:table-cell">
                  {txn.note ?? <span className="text-gray-300">—</span>}
                </td>

                {/* Amount */}
                <td
                  className={`px-4 py-3 text-sm font-medium text-right whitespace-nowrap ${
                    txn.type === "INCOME" ? "text-green-600" : "text-red-600"
                  }`}
                >
                  {txn.type === "INCOME" ? "+" : "-"}
                  {formatAmount(txn.amount)}
                </td>

                {/* Actions */}
                <td className="px-4 py-3 text-right whitespace-nowrap">
                  <div className="flex items-center justify-end gap-3">
                    <button
                      onClick={() => onEdit(txn)}
                      className="text-xs text-blue-500 hover:text-blue-700 transition-colors whitespace-nowrap"
                    >
                      Edit
                    </button>

                    <button
                      onClick={() => onDelete(txn.id)}
                      disabled={isDeleting}
                      className="text-xs text-red-500 hover:text-red-700 disabled:opacity-40 transition-colors whitespace-nowrap"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex flex-col sm:flex-row items-center justify-between gap-2 px-4 py-3 border-t border-gray-100 bg-gray-50">
          {/* Info */}
          <p className="text-xs text-gray-400 text-center sm:text-left">
            Showing {start}–{end} of {pageInfo.totalElements}
          </p>

          {/* Controls */}
          <div className="flex flex-wrap justify-center gap-1">
            <button
              onClick={() => onPageChange(currentPage - 1)}
              disabled={currentPage === 0}
              className="px-3 py-1.5 text-xs border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-white transition-colors"
            >
              ← Prev
            </button>

            {Array.from({ length: totalPages }, (_, i) => i)
              .filter(
                (i) =>
                  i === 0 ||
                  i === totalPages - 1 ||
                  Math.abs(i - currentPage) <= 1,
              )
              .reduce<(number | "ellipsis")[]>((acc, i, idx, arr) => {
                if (idx > 0 && i - (arr[idx - 1] as number) > 1) {
                  acc.push("ellipsis");
                }
                acc.push(i);
                return acc;
              }, [])
              .map((item, idx) =>
                item === "ellipsis" ? (
                  <span
                    key={`ellipsis-${idx}`}
                    className="px-2 py-1.5 text-xs text-gray-400"
                  >
                    …
                  </span>
                ) : (
                  <button
                    key={item}
                    onClick={() => onPageChange(item as number)}
                    className={`px-3 py-1.5 text-xs border rounded-lg transition-colors ${
                      item === currentPage
                        ? "bg-black text-white border-black"
                        : "border-gray-200 hover:bg-white"
                    }`}
                  >
                    {(item as number) + 1}
                  </button>
                ),
              )}

            <button
              onClick={() => onPageChange(currentPage + 1)}
              disabled={currentPage >= totalPages - 1}
              className="px-3 py-1.5 text-xs border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-white transition-colors"
            >
              Next →
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default TransactionTable;
