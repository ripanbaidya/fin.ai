import type { UserProfileResponse } from "../admin.types";
import UserAvatar from "./UserAvatar";
import UserStatusBadge from "./UserStatusBadge";

interface Props {
  user: UserProfileResponse;
  onView: () => void;
}

const UserCard: React.FC<Props> = ({ user, onView }) => (
  <div className="px-4 py-3.5 border-b border-gray-50 last:border-0 flex items-center gap-3">
    <UserAvatar fullName={user.fullName} size="md" />

    <div className="flex-1 min-w-0">
      <p className="text-[13px] font-semibold text-gray-900 truncate leading-tight">
        {user.fullName}
      </p>
      <p className="text-xs text-gray-400 truncate mt-0.5">{user.email}</p>
      <div className="flex items-center gap-2 mt-1.5">
        <UserStatusBadge status={user.status} />
      </div>
    </div>

    <button
      onClick={onView}
      className="shrink-0 text-[12px] font-medium text-gray-500 hover:text-gray-900 border border-gray-200 hover:border-gray-300 px-2.5 py-1.5 rounded-lg transition-colors"
    >
      View
    </button>
  </div>
);

export default UserCard;
