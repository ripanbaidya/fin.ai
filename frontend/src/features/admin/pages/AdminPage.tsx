import { useState } from "react";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { adminService } from "../adminService";
import type { UserProfileResponse } from "../admin.types";

import AdminStatsGrid from "../components/AdminStatsGrid";
import AdminUsersTable from "../components/AdminUsersTable";
import UserDetailModal from "../components/UserDetailModal";

const PAGE_SIZE = 20;

export default function AdminPage() {
  const [page, setPage] = useState(0);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);

  const { data, isLoading, isError, refetch, isFetching } = useAppQuery({
    queryKey: ["admin", "users", page],
    queryFn: () => adminService.getUsers(page, PAGE_SIZE),
  });

  const users: UserProfileResponse[] = data?.data?.items ?? [];
  const totalPages = data?.data?.pagination?.totalPages ?? 0;
  const totalElements = data?.data?.pagination?.totalElements ?? 0;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-0 py-2 sm:py-4 space-y-4 sm:space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-bold text-gray-900">Admin Panel</h1>
        <p className="text-sm text-gray-400 mt-0.5">
          Manage users and view system stats.
        </p>
      </div>

      <AdminStatsGrid />

      <AdminUsersTable
        users={users}
        totalElements={totalElements}
        totalPages={totalPages}
        currentPage={page}
        isLoading={isLoading}
        isError={isError}
        isFetching={isFetching}
        onPageChange={setPage}
        onViewUser={setSelectedUserId}
        onRefresh={refetch}
      />

      {selectedUserId && (
        <UserDetailModal
          userId={selectedUserId}
          onClose={() => setSelectedUserId(null)}
        />
      )}
    </div>
  );
}
