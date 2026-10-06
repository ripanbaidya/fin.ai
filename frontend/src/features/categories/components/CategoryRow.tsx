import { FiEdit2 } from "react-icons/fi";

import type { CategoryResponse } from "../category.types";

interface Props {
  category: CategoryResponse;
  onDelete: (id: string) => void;
  onEdit: (category: CategoryResponse) => void;
  isDeleting: boolean;
}

export default function CategoryRow({
  category,
  onDelete,
  onEdit,
  isDeleting,
}: Props) {
  return (
    <tr className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
      {/* Name */}
      <td className="px-3 sm:px-4 py-3 text-sm text-gray-800">
        <span className="break-words">{category.name}</span>
        {category.isDefault && (
          <span className="ml-2 text-xs text-gray-400 whitespace-nowrap">
            (default)
          </span>
        )}
      </td>

      {/* Type badge */}
      <td className="px-3 sm:px-4 py-3 whitespace-nowrap">
        <span
          className={`inline-block text-xs font-medium px-2 py-0.5 rounded-full ${
            category.categoryType === "INCOME"
              ? "bg-green-50 text-green-700"
              : "bg-red-50 text-red-600"
          }`}
        >
          {category.categoryType}
        </span>
      </td>

      {/* Actions */}
      <td className="px-3 sm:px-4 py-3 text-right">
        {!category.isDefault ? (
          <div className="flex justify-end items-center gap-3">
            <button
              onClick={() => onEdit(category)}
              className="text-gray-500 hover:text-black transition-colors p-1 -m-1"
              aria-label="Edit category"
            >
              <FiEdit2 size={14} />
            </button>

            <button
              onClick={() => onDelete(category.id)}
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
}
