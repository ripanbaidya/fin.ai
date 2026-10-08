import { RiLogoutBoxRLine } from "react-icons/ri";
import { getInitials } from "../../utils/profileHelpers";

interface Props {
  displayName: string;
  onLogout: () => void;
  collapsed?: boolean;
}

const SidebarUserFooter: React.FC<Props> = ({
  displayName,
  onLogout,
  collapsed = false,
}) => {
  const initials = getInitials(displayName);

  if (collapsed) {
    return (
      <div className="flex flex-col items-center gap-2 border-t border-gray-100 p-2">
        <div
          aria-label={displayName}
          className="flex h-8 w-8 items-center justify-center rounded-full bg-black"
        >
          <span className="select-none text-xs font-black text-white">
            {initials}
          </span>
        </div>
        <button
          type="button"
          onClick={onLogout}
          title="Sign out"
          aria-label="Sign out"
          className="flex h-10 w-10 items-center justify-center rounded-full text-red-600 transition-colors hover:bg-red-50 hover:text-red-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-600 focus-visible:ring-offset-2"
        >
          <RiLogoutBoxRLine size={18} aria-hidden="true" />
        </button>
      </div>
    );
  }

  return (
    <div className="border-t border-gray-100 p-3">
      <div className="flex items-center gap-3 rounded-lg px-2 py-2 transition-colors hover:bg-gray-50">
        <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-black">
          <span className="select-none text-sm font-black text-white">
            {initials}
          </span>
        </div>
        <div className="min-w-0 flex-1">
          <p className="truncate text-[13px] font-medium leading-tight text-gray-900">
            {displayName}
          </p>
        </div>
        <button
          type="button"
          onClick={onLogout}
          title="Sign out"
          aria-label="Sign out"
          className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-red-600 transition-colors hover:bg-red-50 hover:text-red-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-600 focus-visible:ring-offset-2"
        >
          <RiLogoutBoxRLine size={18} aria-hidden="true" />
        </button>
      </div>
    </div>
  );
};

export default SidebarUserFooter;
