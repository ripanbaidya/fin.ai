import PaymentModeRow from "./PaymentModeRow";
import Spinner from "../../../shared/components/ui/Spinner";
import { QueryError } from "../../../shared/components/ui/QueryError";
import { FieldErrorMessage } from "../../../shared/components/ui/FieldErrorMessage";
import { FormError } from "../../../shared/components/ui/FormError";

import type { PaymentModeResponse } from "../paymentMode.types";
import type { AppError } from "../../../api/errorParser";

interface Props {
  showForm: boolean;

  name: string;
  setName: (v: string) => void;

  editingMode: PaymentModeResponse | null;

  fieldErrors: Record<string, string>;
  formError: string | null;

  modes: PaymentModeResponse[];

  isLoading: boolean;
  error: AppError | null;
  refetch: () => void;

  isCreating: boolean;
  isUpdating: boolean;
  isDeleting: boolean;

  handleSubmit: (e: React.FormEvent) => void;
  handleEdit: (mode: PaymentModeResponse) => void;
  handleDelete: (id: string) => void;
  resetForm: () => void;

  openForm: () => void;
}

const PaymentModesView: React.FC<Props> = (props) => {
  const {
    showForm,
    name,
    setName,
    editingMode,
    fieldErrors,
    formError,
    modes,
    isLoading,
    error,
    refetch,
    isCreating,
    isUpdating,
    isDeleting,
    handleSubmit,
    handleEdit,
    handleDelete,
    resetForm,
    openForm,
  } = props;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mb-6">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Payment Modes</h1>
          <p className="text-sm text-gray-500 mt-1">
            Manage your payment methods.
          </p>
        </div>

        {!showForm && (
          <button
            onClick={openForm}
            className="text-sm bg-black text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors w-full sm:w-auto"
          >
            + New Payment Mode
          </button>
        )}
      </div>

      {/* Inline form */}
      {showForm && (
        <div className="bg-white border border-gray-200 rounded-lg p-4 sm:p-5 mb-6">
          <h2 className="text-sm font-medium text-gray-700 mb-4">
            {editingMode ? "Edit Payment Mode" : "New Payment Mode"}
          </h2>

          <FormError error={formError} />

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="text-sm text-gray-600 block mb-1">Name</label>
              <input
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. UPI, Cash, Card"
                className="w-full px-3 py-2 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-black"
              />
              <FieldErrorMessage message={fieldErrors.name} />
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                disabled={isCreating || isUpdating}
                className="bg-black text-white px-4 py-2 rounded-lg text-sm disabled:opacity-60 hover:bg-gray-800 transition-colors"
              >
                {editingMode
                  ? isUpdating
                    ? "Updating..."
                    : "Update"
                  : isCreating
                    ? "Creating..."
                    : "Create"}
              </button>

              <button
                type="button"
                onClick={resetForm}
                className="px-4 py-2 text-sm border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Table */}
      <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
        {isLoading ? (
          <div className="flex justify-center py-10">
            <Spinner />
          </div>
        ) : error ? (
          <div className="p-6">
            <QueryError error={error} onRetry={refetch} />
          </div>
        ) : modes.length === 0 ? (
          <div className="py-10 text-center text-sm text-gray-400">
            No payment modes yet.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[320px]">
              <thead>
                <tr className="text-left text-xs text-gray-400 bg-gray-50">
                  <th className="px-3 sm:px-4 py-3 font-medium">Name</th>
                  <th className="px-3 sm:px-4 py-3 text-right font-medium">
                    Action
                  </th>
                </tr>
              </thead>
              <tbody>
                {modes.map((mode) => (
                  <PaymentModeRow
                    key={mode.id}
                    mode={mode}
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

      {/* Footer count */}
      {modes.length > 0 && (
        <p className="text-xs text-gray-400 mt-3 text-right">
          {modes.length} mode{modes.length === 1 ? "" : "s"} ·{" "}
          {modes.filter((m) => !m.isDefault).length} custom
        </p>
      )}
    </div>
  );
};

export default PaymentModesView;
