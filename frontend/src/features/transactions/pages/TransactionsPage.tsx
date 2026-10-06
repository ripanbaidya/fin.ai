import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { transactionService } from "../transactionService";
import { categoryService } from "../../categories/categoryService";
import { paymentModeService } from "../../payment-modes/paymentModeService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";
import { FiDownload } from "react-icons/fi";

import TransactionFilters from "../components/TransactionFilters";
import { DEFAULT_FILTERS, type FilterState } from "../transactionFilters.types";
import TransactionTable from "../components/TransactionTable";
import TransactionForm from "../components/TransactionForm";

import type {
  CreateTransactionRequest,
  TransactionResponse,
  UpdateTransactionRequest,
} from "../transaction.types";
import type { PaginationMeta } from "../../../types/api.types";

/* Constants */
const PAGE_SIZE = 20;

const currentMonth = () => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
};

const monthRange = (month: string) => {
  const [year, mon] = month.split("-").map(Number);
  const start = new Date(year, mon - 1, 1);
  const end = new Date(year, mon, 0);
  const fmt = (d: Date) =>
    `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(
      d.getDate(),
    ).padStart(2, "0")}`;
  return { dateFrom: fmt(start), dateTo: fmt(end) };
};

const EMPTY_PAGE_INFO: PaginationMeta = {
  page: 0,
  size: PAGE_SIZE,
  totalElements: 0,
  totalPages: 0,
  first: true,
  last: true,
  hasNext: false,
  hasPrevious: false,
};

/* Page */
export default function TransactionsPage() {
  const queryClient = useQueryClient();

  const [month, setMonth] = useState(currentMonth());
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState<FilterState>({
    ...DEFAULT_FILTERS,
    ...monthRange(currentMonth()),
  });

  const [editing, setEditing] = useState<TransactionResponse | null | false>(
    null,
  );
  const [submitError, setSubmitError] = useState<AppError | null>(null);
  const [isExporting, setIsExporting] = useState(false);

  const isFormOpen = editing !== null;
  const isEditMode = editing !== null && editing !== false;

  const txnQuery = useAppQuery({
    queryKey: ["transactions", page, filters],
    queryFn: () =>
      transactionService.getAll({
        page,
        size: PAGE_SIZE,
        type: filters.type || undefined,
        dateFrom: filters.dateFrom || undefined,
        dateTo: filters.dateTo || undefined,
      }),
    placeholderData: (prev) => prev,
  });

  const categoryIncomeQuery = useAppQuery({
    queryKey: ["categories", "INCOME"],
    queryFn: () => categoryService.getAll("INCOME"),
  });

  const categoryExpenseQuery = useAppQuery({
    queryKey: ["categories", "EXPENSE"],
    queryFn: () => categoryService.getAll("EXPENSE"),
  });

  const paymentModeQuery = useAppQuery({
    queryKey: ["payment-modes"],
    queryFn: () => paymentModeService.getAll(),
  });

  const handleExport = async () => {
    setIsExporting(true);
    try {
      await transactionService.exportCsv();
    } catch (e) {
      console.error("Export failed", e);
    } finally {
      setIsExporting(false);
    }
  };

  const { mutate: create, isPending: isCreating } = useAppMutation({
    mutationFn: (data: CreateTransactionRequest) =>
      transactionService.create(data),
    onSuccess: () => {
      closeForm();
      setPage(0);
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  const { mutate: update, isPending: isUpdating } = useAppMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: UpdateTransactionRequest;
    }) => transactionService.update(id, data),
    onSuccess: () => {
      closeForm();
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => setSubmitError(err),
  });

  const { mutate: remove, isPending: isDeleting } = useAppMutation({
    mutationFn: (id: string) => transactionService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
    onError: (err: AppError) => console.error("Delete failed", err.message),
  });

  const openCreate = () => {
    setSubmitError(null);
    setEditing(false);
  };

  const openEdit = (txn: TransactionResponse) => {
    setSubmitError(null);
    setEditing(txn);
  };

  const closeForm = () => {
    setEditing(null);
    setSubmitError(null);
  };

  const handleFilterChange = (next: FilterState) => {
    setFilters(next);
    setPage(0);
  };

  const handleFilterReset = () => {
    const resetMonth = currentMonth();
    setMonth(resetMonth);
    setFilters({
      ...DEFAULT_FILTERS,
      ...monthRange(resetMonth),
    });
    setPage(0);
  };

  const handleMonthChange = (nextMonth: string) => {
    setMonth(nextMonth);
    setFilters((prev) => ({
      ...prev,
      ...monthRange(nextMonth),
    }));
    setPage(0);
  };

  const transactions = txnQuery.data?.data?.items ?? [];
  const pageInfo: PaginationMeta = txnQuery.data?.data?.pagination ?? EMPTY_PAGE_INFO;

  const allCategories = [
    ...(categoryIncomeQuery.data?.data ?? []),
    ...(categoryExpenseQuery.data?.data ?? []),
  ];

  const paymentModes = paymentModeQuery.data?.data ?? [];

  const isPageLoading = txnQuery.isLoading;
  const isPageRefetching = txnQuery.isFetching && !txnQuery.isLoading;
  const isPending = isCreating || isUpdating;

  return (
    // Added padding for mobile safety
    <div className="max-w-6xl mx-auto w-full px-4 sm:px-6 lg:px-0 space-y-5">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        {/* LEFT */}
        <div className="min-w-0">
          <h1 className="text-lg sm:text-xl font-semibold text-gray-900 truncate">
            Transactions
          </h1>

          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            {pageInfo.totalElements > 0
              ? `${pageInfo.totalElements} transaction${
                  pageInfo.totalElements === 1 ? "" : "s"
                } total`
              : "Track your income and expenses."}
          </p>
        </div>

        {/* RIGHT ACTIONS */}
        <div className="flex flex-wrap sm:flex-nowrap items-center gap-2 w-full sm:w-auto">
          {/* Export */}
          <button
            onClick={handleExport}
            disabled={isExporting}
            className="flex items-center justify-center gap-2 w-full sm:w-auto text-sm font-medium border border-gray-200 text-gray-700 px-3 sm:px-4 py-2 rounded-lg 
            hover:bg-gray-100 hover:border-gray-300 transition-all duration-200 
            disabled:opacity-50 disabled:cursor-not-allowed active:scale-[0.98]"
          >
            {isExporting ? (
              <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
            ) : (
              <FiDownload className="text-base" />
            )}
            <span className="whitespace-nowrap">
              {isExporting ? "Exporting..." : "Export CSV"}
            </span>
          </button>

          {/* New Transaction */}
          <button
            onClick={openCreate}
            className="w-full sm:w-auto text-sm bg-black text-white px-3 sm:px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors whitespace-nowrap"
          >
            + New Transaction
          </button>
        </div>
      </div>

      {/* Filters */}
      <TransactionFilters
        filters={filters}
        month={month}
        onMonthChange={handleMonthChange}
        onChange={handleFilterChange}
        onReset={handleFilterReset}
      />

      {/* Content */}
      {isPageLoading ? (
        <div className="flex justify-center py-16">
          <Spinner />
        </div>
      ) : txnQuery.error ? (
        <QueryError error={txnQuery.error} onRetry={() => txnQuery.refetch()} />
      ) : transactions.length === 0 ? (
        <div className="bg-white border border-gray-200 rounded-lg py-16 text-center px-4">
          <p className="text-sm text-gray-400 mb-3">
            {filters.type || filters.dateFrom || filters.dateTo
              ? "No transactions match the current filters."
              : "No transactions yet."}
          </p>

          {!(filters.type || filters.dateFrom || filters.dateTo) && (
            <button
              onClick={openCreate}
              className="text-sm text-black underline underline-offset-2"
            >
              Add your first transaction
            </button>
          )}
        </div>
      ) : (
        <div className="relative w-full">
          {isPageRefetching && (
            <div className="absolute inset-0 bg-white/60 z-10 flex items-center justify-center rounded-lg">
              <Spinner />
            </div>
          )}

          <TransactionTable
            transactions={transactions}
            pageInfo={pageInfo}
            currentPage={page}
            onPageChange={setPage}
            onEdit={openEdit}
            onDelete={(id) => remove(id)}
            isDeleting={isDeleting}
          />
        </div>
      )}

      {/* Form */}
      {isFormOpen &&
        (isEditMode ? (
          <TransactionForm
            mode="edit"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: UpdateTransactionRequest) =>
              update({ id: (editing as TransactionResponse).id, data })
            }
            isPending={isPending}
            submitError={submitError}
            initialData={editing as TransactionResponse}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ) : (
          <TransactionForm
            mode="create"
            open={isFormOpen}
            onClose={closeForm}
            onSubmit={(data: CreateTransactionRequest) => create(data)}
            isPending={isPending}
            submitError={submitError}
            categories={allCategories}
            paymentModes={paymentModes}
          />
        ))}
    </div>
  );
}
