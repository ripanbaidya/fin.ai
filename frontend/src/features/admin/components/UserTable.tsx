import type { UserProfileResponse } from "../admin.types";
import UserCard from "./UserCard";
import UserTableRow from "./UserTableRow";

interface Props {
  users: UserProfileResponse[];
  onViewUser: (id: string) => void;
}

const TABLE_HEADERS = ["User", "Email", "Account status", ""];

const UserTable: React.FC<Props> = ({ users, onViewUser }) => (
  <>
    {/* Mobile: card list */}
    <div className="sm:hidden">
      {users.map((user) => (
        <UserCard
          key={user.id}
          user={user}
          onView={() => onViewUser(user.id)}
        />
      ))}
    </div>

    {/* sm+: full table */}
    <div className="hidden sm:block overflow-x-auto">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-left border-b border-gray-100 bg-gray-50/50">
            {TABLE_HEADERS.map((header, i) => (
              <th
                key={i}
                className="px-5 py-3 text-[11px] font-semibold text-gray-400 uppercase tracking-wider"
              >
                {header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-50">
          {users.map((user) => (
            <UserTableRow
              key={user.id}
              user={user}
              onView={() => onViewUser(user.id)}
            />
          ))}
        </tbody>
      </table>
    </div>
  </>
);

export default UserTable;
