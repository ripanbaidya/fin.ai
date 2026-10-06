import { FiEdit2 } from "react-icons/fi";

import type { PaymentModeResponse } from "../paymentMode.types";

interface Props {
  mode: PaymentModeResponse;
  onDelete: (id: string) => void;
  onEdit: (mode: PaymentModeResponse) => void;
  isDeleting: boolean;
}

const PaymentModeRow: React.FC<Props> = ({
  mode,
  onDelete,
  onEdit,
  isDeleting,
}) => (
  <tr className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
    {/* Name */}
    <td className="px-3 sm:px-4 py-3 text-sm text-gray-800">
      <span className="break-words">{mode.name}</span>
      {mode.isDefault && (
        <span className="ml-2 text-xs text-gray-400 whitespace-nowrap">
          (default)
        </span>
      )}
    </td>

    {/* Actions */}
    <td className="px-3 sm:px-4 py-3 text-right">
      {!mode.isDefault ? (
        <div className="flex justify-end items-center gap-3">
          <button
            onClick={() => onEdit(mode)}
            className="text-gray-500 hover:text-black transition-colors p-1 -m-1"
            aria-label="Edit payment mode"
          >
            <FiEdit2 size={14} />
          </button>

          <button
            onClick={() => onDelete(mode.id)}
            disabled={isDeleting}
            className="text-xs text-red-500 hover:text-red-700 disabled:opacity-40 transition-colors"
          >
            Delete
          </button>
        </div>
      ) : (
        <span className="text-xs text-gray-300">—</span>
      )}
    </td>
  </tr>
);

export default PaymentModeRow;
