import { RiCloseLine, RiShieldUserLine } from "react-icons/ri";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { adminService } from "../adminService";

interface Props {
  userId: string;
  onClose: () => void;
}

interface DetailRowProps {
  label: string;
  value: string;
}

const DetailRow: React.FC<DetailRowProps> = ({ label, value }) => {
  const isStatus = label === "Account Status";

  const valueColor =
    isStatus && value === "ACTIVE"
      ? "text-green-600"
      : isStatus && value === "PENDING_VERIFICATION"
        ? "text-amber-600"
      : isStatus && value !== "ACTIVE"
        ? "text-gray-400"
        : "text-gray-800";

  return (
    <div className="flex gap-3">
      <dt className="w-28 shrink-0 text-gray-400 text-[12px] font-medium pt-0.5">
        {label}
      </dt>
      <dd className={`break-all text-[13px] font-medium ${valueColor}`}>
        {value}
      </dd>
    </div>
  );
};

const SkeletonDetailRows: React.FC = () => (
  <div className="space-y-3">
    {[...Array(5)].map((_, i) => (
      <div key={i} className="flex gap-3 items-center">
        <div className="h-4 bg-gray-100 animate-pulse rounded w-24 shrink-0" />
        <div className="h-4 bg-gray-100 animate-pulse rounded flex-1" />
      </div>
    ))}
  </div>
);

const UserDetailModal: React.FC<Props> = ({ userId, onClose }) => {
  const { data, isLoading } = useAppQuery({
    queryKey: ["admin", "user", userId],
    queryFn: () => adminService.getUserById(userId),
  });

  const user = data?.data;

  const rows: [string, string][] = user
    ? [
        ["ID", user.id],
        ["Full Name", user.fullName],
        ["Email", user.email],
        ["Account Status", user.status],
      ]
    : [];

  return (
    <div
      className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/50 px-0 sm:px-4"
      onClick={onClose}
    >
      <div
        className="bg-white rounded-t-2xl sm:rounded-xl border border-gray-100 w-full sm:max-w-md shadow-2xl"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-5 sm:px-6 py-4 border-b border-gray-100">
          <div className="flex items-center gap-2">
            <span className="text-gray-900">
              <RiShieldUserLine size={18} />
            </span>
            <h2 className="text-sm font-semibold text-gray-900">
              User Details
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-gray-400 hover:bg-gray-100 hover:text-gray-600 transition-colors"
          >
            <RiCloseLine size={18} />
          </button>
        </div>

        {/* Body */}
        <div className="px-5 sm:px-6 py-5">
          {isLoading ? (
            <SkeletonDetailRows />
          ) : user ? (
            <dl className="space-y-3 text-sm">
              {rows.map(([key, val]) => (
                <DetailRow key={key} label={key} value={val} />
              ))}
            </dl>
          ) : (
            <p className="text-sm text-gray-400 text-center py-4">
              User not found.
            </p>
          )}
        </div>

        {/* Footer */}
        <div className="px-5 sm:px-6 pb-[calc(1.25rem_+_env(safe-area-inset-bottom))]">
          <button
            onClick={onClose}
            className="w-full py-2.5 bg-gray-900 text-white text-sm font-medium rounded-xl hover:bg-gray-700 transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserDetailModal;
