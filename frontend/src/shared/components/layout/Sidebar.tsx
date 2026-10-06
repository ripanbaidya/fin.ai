import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { useAppQuery } from "../../hooks/useAppQuery";
import { getInitials } from "../../utils/profileHelpers";
import { authService } from "../../../features/auth/authService";
import { userService } from "../../../features/profile/profileService";
import { toast } from "sonner";
import { RiRepeat2Line } from "react-icons/ri";
import { ROUTES } from "../../../routes/routePaths";

import {
  RiDashboardLine,
  RiExchangeDollarLine,
  RiPriceTag3Line,
  RiBankCardLine,
  RiPieChartLine,
  RiGovernmentLine,
  RiChatSmile2Line,
  RiUserLine,
  RiInformationLine,
  RiLogoutBoxRLine,
  RiMenuLine,
  RiCloseLine,
  RiShieldUserLine,
} from "react-icons/ri";

import NotificationBell from "../../../features/notifications/components/NotificationBell";

type NavItem = {
  label: string;
  path: string;
  icon: React.ElementType;
};

type NavGroup = {
  heading?: string;
  items: NavItem[];
};

const NAV_GROUPS: NavGroup[] = [
  {
    heading: "Overview",
    items: [
      { label: "Dashboard", path: ROUTES.dashboard, icon: RiDashboardLine },
    ],
  },
  {
    heading: "Money",
    items: [
      {
        label: "Transactions",
        path: ROUTES.transactions,
        icon: RiExchangeDollarLine,
      },
      { label: "Recurring", path: ROUTES.recurring, icon: RiRepeat2Line },
      { label: "Budgets", path: ROUTES.budgets, icon: RiPieChartLine },
      { label: "Savings Goals", path: ROUTES.savings, icon: RiGovernmentLine },
    ],
  },
  {
    heading: "Settings",
    items: [
      { label: "Categories", path: ROUTES.categories, icon: RiPriceTag3Line },
      {
        label: "Payment Modes",
        path: ROUTES.paymentModes,
        icon: RiBankCardLine,
      },
    ],
  },
  {
    heading: "More",
    items: [
      { label: "Chat", path: ROUTES.chat, icon: RiChatSmile2Line },
      { label: "Profile", path: ROUTES.profile, icon: RiUserLine },
      { label: "About", path: ROUTES.about, icon: RiInformationLine },
    ],
  },
];

const NAV_ITEMS_FLAT: NavItem[] = NAV_GROUPS.flatMap((g) => g.items);

const ADMIN_NAV_ITEM: NavItem = {
  label: "Admin Panel",
  path: ROUTES.admin,
  icon: RiShieldUserLine,
};

const BOTTOM_TAB_PATHS = [
  "/dashboard",
  "/transactions",
  "/budgets",
  "/chat",
  "/profile",
];
const BOTTOM_TAB_ITEMS = NAV_ITEMS_FLAT.filter((item) =>
  BOTTOM_TAB_PATHS.includes(item.path),
);

const FullNavLink: React.FC<{ item: NavItem; onClick?: () => void }> = ({
  item,
  onClick,
}) => {
  const Icon = item.icon;
  return (
    <NavLink
      to={item.path}
      onClick={onClick}
      className={({ isActive }) =>
        `flex items-center gap-3 px-3 py-2.5 rounded-lg text-[13.5px] transition-all duration-150 ${
          isActive
            ? "bg-blue-50 text-blue-700 font-medium"
              : "text-gray-600 hover:text-gray-900 hover:bg-gray-50"
        }`
      }
    >
      {({ isActive }) => (
        <>
          <span className={isActive ? "text-blue-700" : "text-gray-400"}>
            <Icon size={17} />
          </span>
          {item.label}
        </>
      )}
    </NavLink>
  );
};

const IconNavLink: React.FC<{ item: NavItem }> = ({ item }) => {
  const Icon = item.icon;
  return (
    <NavLink
      to={item.path}
      title={item.label}
      className={({ isActive }) =>
        `flex items-center justify-center w-10 h-10 rounded-lg transition-all duration-150 ${
          isActive
            ? "bg-blue-50 text-blue-700"
            : "text-gray-400 hover:text-gray-900 hover:bg-gray-50"
        }`
      }
    >
      <Icon size={19} />
    </NavLink>
  );
};

const GroupedNav: React.FC<{
  onItemClick?: () => void;
  showAdmin: boolean;
}> = ({ onItemClick, showAdmin }) => (
  <nav className="flex-1 px-3 py-4 overflow-y-auto space-y-4">
    {NAV_GROUPS.map((group) => (
      <div key={group.heading}>
        {group.heading && (
          <p className="px-3 mb-1 text-[10px] font-semibold uppercase tracking-widest text-gray-400 select-none">
            {group.heading}
          </p>
        )}
        <div className="space-y-0.5">
          {group.items.map((item) => (
            <FullNavLink key={item.path} item={item} onClick={onItemClick} />
          ))}
        </div>
      </div>
    ))}

    {showAdmin && (
      <div>
        <div className="mx-3 my-1 border-t border-gray-100" />
        <FullNavLink item={ADMIN_NAV_ITEM} onClick={onItemClick} />
      </div>
    )}
  </nav>
);

const UserFooter: React.FC<{
  displayName: string;
  logout: () => void;
  collapsed?: boolean;
}> = ({ displayName, logout, collapsed = false }) => {
  const initials = getInitials(displayName);

  if (collapsed) {
    return (
      <div className="border-t border-gray-100 p-2 flex flex-col items-center gap-2">
        <div className="w-8 h-8 rounded-full bg-[#e8ff4f] flex items-center justify-center">
          <span className="text-gray-900 text-xs font-black select-none">
            {initials}
          </span>
        </div>
        <button
          onClick={logout}
          title="Sign out"
          className="text-gray-300 hover:text-red-500 transition-colors p-1"
        >
          <RiLogoutBoxRLine size={16} />
        </button>
      </div>
    );
  }

  return (
    <div className="border-t border-gray-100 p-3">
      <div className="flex items-center gap-3 px-2 py-2 rounded-lg hover:bg-gray-50 transition-colors">
        <div className="w-8 h-8 rounded-full bg-[#e8ff4f] flex items-center justify-center shrink-0">
          <span className="text-gray-900 text-xs font-black select-none">
            {initials}
          </span>
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-medium text-gray-900 truncate leading-tight">
            {displayName}
          </p>
          <button
            onClick={logout}
            className="text-xs text-gray-400 hover:text-red-500 transition-colors mt-0.5"
          >
            Sign out
          </button>
        </div>
        <button
          onClick={logout}
          title="Sign out"
          className="shrink-0 text-gray-300 hover:text-red-500 transition-colors p-1 rounded"
        >
          <RiLogoutBoxRLine size={15} />
        </button>
      </div>
    </div>
  );
};

const Sidebar: React.FC = () => {
  const { user, logout, isAdmin, refreshToken } = useAuth();
  const { data: profileData } = useAppQuery({
    queryKey: ["profile"],
    queryFn: () => userService.getProfile(),
    enabled: Boolean(user?.id),
  });
  const displayName =
    profileData?.data.fullName ||
    user?.fullName ||
    user?.email?.split("@")[0] ||
    "Account";
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [drawerOpen, setDrawerOpen] = useState(false);

  const closeDrawer = () => setDrawerOpen(false);
  const handleLogout = async () => {
    try {
      if (refreshToken) await authService.logout({ refreshToken });
    } catch (error) {
      toast.error(error instanceof Error ? error.message : "Could not revoke this session.");
    } finally {
      logout();
      queryClient.clear();
      navigate(ROUTES.login);
    }
  };

  return (
    <>
      <aside className="hidden lg:flex w-60 min-h-screen bg-white border-r border-gray-100 flex-col">
        <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
          <span
            className="text-xl font-bold tracking-tight text-gray-900 cursor-pointer select-none"
            onClick={() => navigate("/home")}
          >
            fin
          </span>
          <div className="flex items-center gap-1">
            <NotificationBell align="left" />
          </div>
        </div>

        <GroupedNav showAdmin={isAdmin} />

        <UserFooter displayName={displayName} logout={handleLogout} />
      </aside>

      <aside className="hidden md:flex lg:hidden w-16 min-h-screen bg-white border-r border-gray-100 flex-col items-center">
        <div
          className="w-full flex items-center justify-center py-5 border-b border-gray-100 cursor-pointer select-none"
          onClick={() => navigate("/home")}
        >
          <span className="text-xl font-black text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            f
          </span>
        </div>

        <div className="py-2 flex flex-col items-center gap-1 border-b border-gray-100 w-full">
          <NotificationBell size="sm" align="left" />
        </div>

        <nav className="flex-1 flex flex-col items-center py-4 gap-1 overflow-y-auto w-full">
          {NAV_ITEMS_FLAT.map((item) => (
            <IconNavLink key={item.path} item={item} />
          ))}
          {isAdmin && (
            <>
              <div className="w-6 my-1 border-t border-gray-100" />
              <IconNavLink item={ADMIN_NAV_ITEM} />
            </>
          )}
        </nav>

        <UserFooter displayName={displayName} logout={handleLogout} collapsed />
      </aside>

      <div className="md:hidden fixed top-0 left-0 right-0 z-40 bg-white border-b border-gray-100 flex items-center justify-between px-4 h-14">
        <span
          className="text-lg font-bold tracking-tight text-gray-900 cursor-pointer select-none"
          onClick={() => navigate("/home")}
        >
          fin
        </span>
        <div className="flex items-center gap-1">
          <NotificationBell align="right" />
          <button
            onClick={() => setDrawerOpen(true)}
            className="p-2 rounded-lg text-gray-500 hover:bg-gray-50 transition-colors"
            aria-label="Open menu"
          >
            <RiMenuLine size={22} />
          </button>
        </div>
      </div>

      <div className="md:hidden h-14 shrink-0" />

      {drawerOpen && (
        <>
          <div
            className="md:hidden fixed inset-0 bg-black/40 z-50"
            onClick={closeDrawer}
          />
          <div className="md:hidden fixed top-0 left-0 bottom-0 w-72 bg-white z-50 flex flex-col shadow-2xl">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
              <span
                className="text-xl font-bold tracking-tight text-gray-900 cursor-pointer"
                onClick={() => {
                  navigate("/home");
                  closeDrawer();
                }}
              >
                fin
              </span>
              <button
                onClick={closeDrawer}
                className="p-2 rounded-lg text-gray-400 hover:bg-gray-50 transition-colors"
                aria-label="Close menu"
              >
                <RiCloseLine size={20} />
              </button>
            </div>

            <GroupedNav onItemClick={closeDrawer} showAdmin={isAdmin} />

            <UserFooter displayName={displayName} logout={handleLogout} />
          </div>
        </>
      )}

      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white border-t border-gray-100 flex items-center justify-around px-2 h-[calc(4rem_+_env(safe-area-inset-bottom))] pb-[env(safe-area-inset-bottom)]">
        {BOTTOM_TAB_ITEMS.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex flex-col items-center gap-0.5 px-3 py-1.5 rounded-xl transition-colors ${
                  isActive ? "text-gray-900" : "text-gray-400"
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <span
                    className={`p-1.5 rounded-xl transition-colors ${
                      isActive ? "bg-blue-50" : ""
                    }`}
                  >
                    <Icon
                      size={19}
                      className={isActive ? "text-blue-700" : ""}
                    />
                  </span>
                  <span className="text-[10px] font-medium leading-none">
                    {item.label}
                  </span>
                </>
              )}
            </NavLink>
          );
        })}
      </nav>

      <div className="md:hidden h-[calc(4rem_+_env(safe-area-inset-bottom))] shrink-0 order-last" />
    </>
  );
};

export default Sidebar;
