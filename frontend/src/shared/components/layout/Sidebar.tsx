import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate } from "react-router-dom";
import { RiCloseLine, RiMenuLine } from "react-icons/ri";
import { toast } from "sonner";
import { authService } from "../../../features/auth/authService";
import { userService } from "../../../features/profile/profileService";
import NotificationBell from "../../../features/notifications/components/NotificationBell";
import { ROUTES } from "../../../routes/routePaths";
import { useAppQuery } from "../../hooks/useAppQuery";
import { useAuth } from "../../hooks/useAuth";
import SidebarUserFooter from "./SidebarUserFooter";
import {
  BottomNavigation,
  CompactSidebarNavigation,
  SidebarNavigation,
} from "./SidebarNavigation";

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
      toast.error(
        error instanceof Error
          ? error.message
          : "Could not revoke this session.",
      );
    } finally {
      logout();
      queryClient.clear();
      navigate(ROUTES.login);
    }
  };

  return (
    <>
      <aside className="hidden min-h-screen w-60 flex-col border-r border-gray-100 bg-white lg:flex">
        <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
          <Link
            to={ROUTES.home}
            className="select-none text-xl font-bold tracking-tight text-gray-900"
          >
            fin
          </Link>
          <NotificationBell align="left" />
        </div>
        <SidebarNavigation isAdmin={isAdmin} />
        <SidebarUserFooter displayName={displayName} onLogout={handleLogout} />
      </aside>

      <aside className="hidden min-h-screen w-16 flex-col items-center border-r border-gray-100 bg-white md:flex lg:hidden">
        <Link
          to={ROUTES.home}
          aria-label="fin home"
          className="flex w-full select-none items-center justify-center border-b border-gray-100 py-5"
        >
          <span className="text-xl font-black text-[#e8ff4f] [-webkit-text-stroke:0.5px_#b8cc00]">
            f
          </span>
        </Link>
        <div className="w-full border-b border-gray-100 py-2">
          <NotificationBell size="sm" align="left" />
        </div>
        <CompactSidebarNavigation isAdmin={isAdmin} />
        <SidebarUserFooter
          displayName={displayName}
          onLogout={handleLogout}
          collapsed
        />
      </aside>

      <div className="fixed left-0 right-0 top-0 z-40 flex h-14 items-center justify-between border-b border-gray-100 bg-white px-4 md:hidden">
        <Link
          to={ROUTES.home}
          className="select-none text-lg font-bold tracking-tight text-gray-900"
        >
          fin
        </Link>
        <div className="flex items-center gap-1">
          <NotificationBell align="right" />
          <button
            type="button"
            onClick={() => setDrawerOpen(true)}
            className="rounded-lg p-2 text-gray-500 transition-colors hover:bg-gray-50"
            aria-label="Open menu"
            aria-expanded={drawerOpen}
          >
            <RiMenuLine size={22} aria-hidden="true" />
          </button>
        </div>
      </div>

      <div className="h-14 shrink-0 md:hidden" />

      {drawerOpen && (
        <>
          <button
            type="button"
            aria-label="Close menu"
            className="fixed inset-0 z-50 bg-black/40 md:hidden"
            onClick={closeDrawer}
          />
          <aside
            aria-label="Mobile navigation drawer"
            className="fixed bottom-0 left-0 top-0 z-50 flex w-72 flex-col bg-white shadow-2xl md:hidden"
          >
            <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
              <Link
                to={ROUTES.home}
                onClick={closeDrawer}
                className="text-xl font-bold tracking-tight text-gray-900"
              >
                fin
              </Link>
              <button
                type="button"
                onClick={closeDrawer}
                className="rounded-lg p-2 text-gray-400 transition-colors hover:bg-gray-50"
                aria-label="Close menu"
              >
                <RiCloseLine size={20} aria-hidden="true" />
              </button>
            </div>
            <SidebarNavigation isAdmin={isAdmin} onItemClick={closeDrawer} />
            <SidebarUserFooter
              displayName={displayName}
              onLogout={handleLogout}
            />
          </aside>
        </>
      )}

      <BottomNavigation isAdmin={isAdmin} />
      <div className="h-[calc(4rem_+_env(safe-area-inset-bottom))] shrink-0 order-last md:hidden" />
    </>
  );
};

export default Sidebar;
