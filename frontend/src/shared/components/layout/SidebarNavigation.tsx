import type { IconType } from "react-icons";
import {
  RiBankCardLine,
  RiChatSmile2Line,
  RiDashboardLine,
  RiExchangeDollarLine,
  RiGovernmentLine,
  RiInformationLine,
  RiPieChartLine,
  RiPriceTag3Line,
  RiRepeat2Line,
  RiShieldUserLine,
  RiUserLine,
} from "react-icons/ri";
import { NavLink } from "react-router-dom";
import { ROUTES } from "../../../routes/routePaths";

interface NavItem {
  label: string;
  path: string;
  icon: IconType;
  showInBottomNav?: boolean;
}

interface NavGroup {
  heading: string;
  items: NavItem[];
}

const NAV_GROUPS: NavGroup[] = [
  {
    heading: "Overview",
    items: [
      {
        label: "Dashboard",
        path: ROUTES.dashboard,
        icon: RiDashboardLine,
        showInBottomNav: true,
      },
    ],
  },
  {
    heading: "Money",
    items: [
      {
        label: "Transactions",
        path: ROUTES.transactions,
        icon: RiExchangeDollarLine,
        showInBottomNav: true,
      },
      { label: "Recurring", path: ROUTES.recurring, icon: RiRepeat2Line },
      {
        label: "Budgets",
        path: ROUTES.budgets,
        icon: RiPieChartLine,
        showInBottomNav: true,
      },
      {
        label: "Savings Goals",
        path: ROUTES.savings,
        icon: RiGovernmentLine,
      },
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
      {
        label: "Chat",
        path: ROUTES.chat,
        icon: RiChatSmile2Line,
        showInBottomNav: true,
      },
      {
        label: "Profile",
        path: ROUTES.profile,
        icon: RiUserLine,
        showInBottomNav: true,
      },
      { label: "About", path: ROUTES.about, icon: RiInformationLine },
    ],
  },
];

const ADMIN_NAV_ITEM: NavItem = {
  label: "Admin Panel",
  path: ROUTES.admin,
  icon: RiShieldUserLine,
  showInBottomNav: true,
};

const USER_NAV_ITEMS = NAV_GROUPS.flatMap(({ items }) => items);

const getNavigationItems = (isAdmin: boolean) =>
  isAdmin ? [ADMIN_NAV_ITEM] : USER_NAV_ITEMS;

const FullNavLink: React.FC<{
  item: NavItem;
  onClick?: () => void;
}> = ({ item, onClick }) => {
  const Icon = item.icon;

  return (
    <NavLink
      to={item.path}
      onClick={onClick}
      className={({ isActive }) =>
        `flex items-center gap-3 rounded-lg px-3 py-2.5 text-[13.5px] transition-all duration-150 ${
          isActive
            ? "bg-blue-50 font-medium text-blue-700"
            : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
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

export const SidebarNavigation: React.FC<{
  isAdmin: boolean;
  onItemClick?: () => void;
}> = ({ isAdmin, onItemClick }) => (
  <nav className="flex-1 space-y-4 overflow-y-auto px-3 py-4">
    {isAdmin ? (
      <FullNavLink item={ADMIN_NAV_ITEM} onClick={onItemClick} />
    ) : (
      NAV_GROUPS.map(({ heading, items }) => (
        <div key={heading}>
          <p className="mb-1 select-none px-3 text-[10px] font-semibold uppercase tracking-widest text-gray-400">
            {heading}
          </p>
          <div className="space-y-0.5">
            {items.map((item) => (
              <FullNavLink
                key={item.path}
                item={item}
                onClick={onItemClick}
              />
            ))}
          </div>
        </div>
      ))
    )}
  </nav>
);

export const CompactSidebarNavigation: React.FC<{ isAdmin: boolean }> = ({
  isAdmin,
}) => (
  <nav className="flex-1 space-y-1 overflow-y-auto py-4">
    {getNavigationItems(isAdmin).map(({ icon: Icon, label, path }) => (
      <NavLink
        key={path}
        to={path}
        title={label}
        aria-label={label}
        className={({ isActive }) =>
          `mx-auto flex h-10 w-10 items-center justify-center rounded-lg transition-all duration-150 ${
            isActive
              ? "bg-blue-50 text-blue-700"
              : "text-gray-400 hover:bg-gray-50 hover:text-gray-900"
          }`
        }
      >
        <Icon size={19} />
      </NavLink>
    ))}
  </nav>
);

export const BottomNavigation: React.FC<{ isAdmin: boolean }> = ({
  isAdmin,
}) => (
  <nav
    aria-label="Bottom navigation"
    className="fixed bottom-0 left-0 right-0 z-40 flex h-[calc(4rem_+_env(safe-area-inset-bottom))] items-center justify-around border-t border-gray-100 bg-white px-2 pb-[env(safe-area-inset-bottom)] md:hidden"
  >
    {getNavigationItems(isAdmin)
      .filter((item) => item.showInBottomNav)
      .map(({ icon: Icon, label, path }) => (
        <NavLink
          key={path}
          to={path}
          className={({ isActive }) =>
            `flex flex-col items-center gap-0.5 rounded-xl px-3 py-1.5 transition-colors ${
              isActive ? "text-gray-900" : "text-gray-400"
            }`
          }
        >
          {({ isActive }) => (
            <>
              <span
                className={`rounded-xl p-1.5 transition-colors ${
                  isActive ? "bg-blue-50" : ""
                }`}
              >
                <Icon
                  size={19}
                  className={isActive ? "text-blue-700" : ""}
                />
              </span>
              <span className="text-[10px] font-medium leading-none">
                {label}
              </span>
            </>
          )}
        </NavLink>
      ))}
  </nav>
);
