import type { UserProfileResponse } from "../admin.types";
import UserAvatar from "./UserAvatar";
import UserStatusBadge from "./UserStatusBadge";

interface Props {
  user: UserProfileResponse;
  onView: () => void;
}

const UserTableRow: React.FC<Props> = ({ user, onView }) => (
  <tr className="hover:bg-gray-50/70 transition-colors">
    <td className="px-5 py-3.5">
      <div className="flex items-center gap-3">
        <UserAvatar fullName={user.fullName} size="sm" />
        <span className="font-medium text-gray-900 text-[13px]">
          {user.fullName}
        </span>
      </div>
    </td>

    <td className="px-5 py-3.5 text-gray-400 text-[13px]">{user.email}</td>

    <td className="px-5 py-3.5">
      <UserStatusBadge status={user.status} />
    </td>

    <td className="px-5 py-3.5">
      <button
        onClick={onView}
        className="text-[12px] font-medium text-gray-500 hover:text-gray-900 border border-gray-200 hover:border-gray-300 px-2.5 py-1 rounded-lg transition-colors"
      >
        View
      </button>
    </td>
  </tr>
);

export default UserTableRow;
