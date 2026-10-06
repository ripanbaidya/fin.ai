import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { adminService } from "../adminService";
import type { AccountStatus, Role } from "../admin.types";

interface Props {
  label: string;
  queryKey: string[];
  role: Role;
  accountStatus: AccountStatus;
}

const StatCard: React.FC<Props> = ({
  label,
  queryKey,
  role,
  accountStatus,
}) => {
  const { data, isLoading } = useAppQuery({
    queryKey,
    queryFn: () => adminService.getUserCount(role, accountStatus),
  });

  return (
    <div
      className="rounded-xl border border-gray-200 bg-white p-4 sm:p-5"
    >
      <p className="text-[11px] font-semibold uppercase tracking-widest mb-2 text-gray-400">
        {label}
      </p>
      <p
        className="text-2xl sm:text-3xl font-bold leading-none text-gray-900"
      >
        {isLoading ? (
          <span
            className="inline-block w-10 h-7 rounded bg-gray-100 animate-pulse"
          />
        ) : (
          (data?.data ?? "—")
        )}
      </p>
    </div>
  );
};

export default StatCard;
