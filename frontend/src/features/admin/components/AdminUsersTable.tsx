import { RiRefreshLine } from "react-icons/ri";
import type { UserProfileResponse } from "../admin.types";
import UserTable from "./UserTable";
import SkeletonRows from "./SkeletonRows";

interface Props {
  users: UserProfileResponse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  isError: boolean;
  isFetching: boolean;
  onPageChange: (page: number) => void;
  onViewUser: (id: string) => void;
  onRefresh: () => void;
}

const AdminUsersTable: React.FC<Props> = ({
  users,
  totalElements,
  totalPages,
  currentPage,
  isLoading,
  isError,
  isFetching,
  onPageChange,
  onViewUser,
  onRefresh,
}) => (
  <div className="bg-white border border-gray-100 rounded-2xl overflow-hidden">
    {/* Toolbar */}
    <div className="px-4 sm:px-5 py-4 border-b border-gray-100 flex items-center justify-between gap-3">
      <div>
        <h2 className="text-sm font-semibold text-gray-900">All Users</h2>
        {totalElements > 0 && (
          <p className="text-xs text-gray-400 mt-0.5">{totalElements} total</p>
        )}
      </div>
      <button
        onClick={onRefresh}
        disabled={isFetching}
        className="flex items-center gap-1.5 text-xs text-gray-500 hover:text-gray-900 border border-gray-200 px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50"
      >
        <RiRefreshLine size={13} className={isFetching ? "animate-spin" : ""} />
        <span className="hidden xs:inline">Refresh</span>
      </button>
    </div>

    {/* States */}
    {isLoading && <SkeletonRows />}

    {isError && (
      <div className="px-5 py-12 text-center">
        <p className="text-sm text-gray-400 mb-3">Failed to load users.</p>
        <button
          onClick={onRefresh}
          className="text-xs text-gray-900 border border-gray-200 px-4 py-2 rounded-lg hover:bg-gray-50 transition-colors"
        >
          Try again
        </button>
      </div>
    )}

    {!isLoading && !isError && users.length === 0 && (
      <div className="px-5 py-12 text-center text-sm text-gray-400">
        No users found.
      </div>
    )}

    {!isLoading && !isError && users.length > 0 && (
      <UserTable users={users} onViewUser={onViewUser} />
    )}

    {/* Pagination */}
    {totalPages > 1 && (
      <div className="px-4 sm:px-5 py-3.5 border-t border-gray-100 flex items-center justify-between gap-3">
        <p className="text-xs text-gray-400">
          Page {currentPage + 1} of {totalPages}
        </p>
        <div className="flex gap-2">
          <button
            disabled={currentPage === 0}
            onClick={() => onPageChange(currentPage - 1)}
            className="text-xs border border-gray-200 px-3 py-1.5 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-50 transition-colors"
          >
            Previous
          </button>
          <button
            disabled={currentPage + 1 >= totalPages}
            onClick={() => onPageChange(currentPage + 1)}
            className="text-xs bg-gray-900 text-white px-3 py-1.5 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-gray-700 transition-colors"
          >
            Next
          </button>
        </div>
      </div>
    )}
  </div>
);

export default AdminUsersTable;
