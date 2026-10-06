import CategoryRow from "./CategoryRow";
import { AppError } from "../../../api/errorParser";

import Spinner from "../../../shared/components/ui/Spinner";
import { QueryError } from "../../../shared/components/ui/QueryError";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";

import type { CategoryResponse, CategoryType } from "../category.types";

interface Props {
  activeType: CategoryType;
  setActiveType: (type: CategoryType) => void;
  showForm: boolean;

  name: string;
  setName: (v: string) => void;

  formType: CategoryType;
  setFormType: (v: CategoryType) => void;

  editingCategory: CategoryResponse | null;

  fieldErrors: Record<string, string>;
  formError: string | null;

  categories: CategoryResponse[];

  isLoading: boolean;
  error: AppError | null;
  refetch: () => void;

  isCreating: boolean;
  isUpdating: boolean;
  isDeleting: boolean;

  handleSubmit: (e: React.FormEvent) => void;
  handleEdit: (cat: CategoryResponse) => void;
  handleDelete: (id: string) => void;

  openForm: () => void;
}

export default function CategoriesView(props: Props) {
  const {
    activeType,
    setActiveType,
    showForm,
    name,
    setName,
    formType,
    setFormType,
    editingCategory,
    fieldErrors,
    formError,
    categories,
    isLoading,
    error,
    refetch,
    isCreating,
    isUpdating,
    isDeleting,
    handleSubmit,
    handleEdit,
    handleDelete,
    openForm,
  } = props;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
        {/* Title + tab toggle */}
        <div className="flex flex-col xs:flex-row xs:items-center gap-3">
          <h1 className="text-xl font-semibold whitespace-nowrap">
            Categories
          </h1>

          <div className="flex border rounded overflow-hidden self-start xs:self-auto">
            {(["EXPENSE", "INCOME"] as CategoryType[]).map((type) => (
              <button
                key={type}
                onClick={() => setActiveType(type)}
                className={`px-3 sm:px-4 py-1.5 text-sm transition-colors ${
                  activeType === type
                    ? "bg-gray-100 text-black"
                    : "text-gray-500 hover:bg-gray-50"
                }`}
              >
                {type}
              </button>
            ))}
          </div>
        </div>

        {/* New button */}
        {!showForm && (
          <button
            onClick={openForm}
            className="bg-black text-white px-4 py-2 rounded text-sm w-full sm:w-auto transition-colors hover:bg-gray-800"
          >
            + New
          </button>
        )}
      </div>

      {/* Inline form */}
      {showForm && (
        <div className="border rounded p-4 mb-6">
          <h2 className="text-sm font-medium mb-3">
            {editingCategory ? "Edit" : "New"} Category
          </h2>

          <FormError error={formError} />

          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Name"
                className="border rounded px-3 py-2 w-full text-sm outline-none focus:ring-2 focus:ring-black"
              />
              <FieldErrorMessage message={fieldErrors.name} />
            </div>

            {/* Type toggle */}
            <div className="flex gap-2">
              {(["EXPENSE", "INCOME"] as CategoryType[]).map((t) => (
                <button
                  key={t}
                  type="button"
                  onClick={() => setFormType(t)}
                  className={`px-3 py-1.5 border rounded text-sm transition-colors ${
                    formType === t
                      ? "bg-black text-white border-black"
                      : "text-gray-600 hover:bg-gray-50"
                  }`}
                >
                  {t}
                </button>
              ))}
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={isCreating || isUpdating}
              className="bg-black text-white px-4 py-2 rounded text-sm w-full sm:w-auto disabled:opacity-60 transition-colors hover:bg-gray-800"
            >
              {editingCategory
                ? isUpdating
                  ? "Updating..."
                  : "Update"
                : isCreating
                  ? "Creating..."
                  : "Create"}
            </button>
          </form>
        </div>
      )}

      {/* Table — horizontally scrollable on small screens */}
      <div className="border rounded overflow-hidden">
        {isLoading ? (
          <Spinner />
        ) : error ? (
          <QueryError error={error} onRetry={refetch} />
        ) : categories.length === 0 ? (
          <p className="text-sm text-gray-400 text-center py-10">
            No {activeType.toLowerCase()} categories yet.
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[360px]">
              <tbody>
                {categories.map((cat) => (
                  <CategoryRow
                    key={cat.id}
                    category={cat}
                    onDelete={handleDelete}
                    onEdit={handleEdit}
                    isDeleting={isDeleting}
                  />
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
