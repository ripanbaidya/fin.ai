import { useEffect, useRef, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { RiLogoutBoxRLine } from "react-icons/ri";
import { toast } from "sonner";

import { userService } from "../profileService";
import { authService } from "../../auth/authService";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import { AppError } from "../../../api/errorParser";
import { QueryError } from "../../../shared/components/ui/QueryError";
import Spinner from "../../../shared/components/ui/Spinner";
import { useAuthStore } from "../../../store/authStore";

import ProfileHeader from "../components/ProfileHeader";
import AccountInfoCard from "../components/AccountInfoCard";
import EmailVerifyPanel from "../components/EmailVerifyPanel";
import DangerZone from "../components/DangerZone";
import { ROUTES } from "../../../routes/routePaths";

export default function ProfilePage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const {
    user: authUser,
    setAuth,
    accessToken,
    refreshToken,
    clearAuth,
  } = useAuthStore();

  const [verifyPanelOpen, setVerifyPanelOpen] = useState(false);
  const [nameSuccess, setNameSuccess] = useState("");
  const [updateError, setUpdateError] = useState<AppError | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const nameSuccessTimeout = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(
    () => () => {
      if (nameSuccessTimeout.current) {
        clearTimeout(nameSuccessTimeout.current);
      }
    },
    [],
  );

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      if (refreshToken) await authService.logout({ refreshToken });
    } catch (error) {
      toast.error(
        error instanceof Error ? error.message : "Could not revoke this session."
      );
    } finally {
      clearAuth();
      queryClient.clear();
      navigate(ROUTES.login);
    }
  };

  const { data, isLoading, error, refetch } = useAppQuery({
    queryKey: ["profile"],
    queryFn: () => userService.getProfile(),
  });

  const profile = data?.data;

  const { mutate: updateName, isPending: isUpdating } = useAppMutation({
    mutationFn: (fullName: string) => userService.updateProfile({ fullName }),
    onSuccess: (res) => {
      if (authUser && accessToken && refreshToken) {
        setAuth(
          { ...authUser, fullName: res.data.fullName },
          accessToken,
          refreshToken,
        );
      }
      queryClient.invalidateQueries({ queryKey: ["profile"] });
      setUpdateError(null);
      setNameSuccess("Name updated successfully.");
      if (nameSuccessTimeout.current) {
        clearTimeout(nameSuccessTimeout.current);
      }
      nameSuccessTimeout.current = setTimeout(() => setNameSuccess(""), 3000);
    },
    onError: (err: AppError) => setUpdateError(err),
  });

  const { mutate: deleteAccount, isPending: isDeletingAccount } = useAppMutation({
    mutationFn: (password: string) => userService.deleteAccount({ password }),
    onSuccess: () => {
      clearAuth();
      queryClient.clear();
      navigate(ROUTES.home);
    },
    onError: (err: AppError) => setDeleteError(err.message),
  });

  if (isLoading) {
    return (
      <div className="flex justify-center py-24">
        <Spinner />
      </div>
    );
  }

  if (error) return <QueryError error={error} onRetry={refetch} />;
  if (!profile) {
    return (
      <p role="alert" className="py-8 text-center text-sm text-gray-500">
        Profile information is unavailable. Please try again.
      </p>
    );
  }

  return (
    <div className="mx-auto w-full min-w-0 max-w-xl py-2 sm:py-4 space-y-4">
      {/* Page Header & Logout Action in one row */}
      <div className="flex items-start justify-between">
        <div>
          <h1 className="text-xl font-semibold text-gray-900">Profile</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            Manage your account details.
          </p>
        </div>

        <button
          type="button"
          onClick={handleLogout}
          disabled={isLoggingOut}
          className="inline-flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-xs font-medium text-red-600 transition-colors hover:bg-red-50 hover:text-red-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-600 disabled:opacity-50"
        >
          <RiLogoutBoxRLine aria-hidden="true" size={15} />
          {isLoggingOut ? "Signing out…" : "Sign out"}
        </button>
      </div>

      <ProfileHeader
        profile={profile}
        isUpdating={isUpdating}
        onUpdateName={updateName}
        nameSuccess={nameSuccess}
        updateError={updateError}
      />

      <AccountInfoCard
        profile={profile}
        onOpenVerify={() => setVerifyPanelOpen(true)}
      />

      <DangerZone
        onDeleteAccount={(password) => {
          setDeleteError(null);
          deleteAccount(password);
        }}
        isDeleting={isDeletingAccount}
        errorMessage={deleteError}
      />

      {verifyPanelOpen && (
        <EmailVerifyPanel
          email={profile.email}
          onClose={() => setVerifyPanelOpen(false)}
          onVerified={() => {
            queryClient.invalidateQueries({ queryKey: ["profile"] });
            setVerifyPanelOpen(false);
          }}
        />
      )}
    </div>
  );
}