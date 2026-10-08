import { useState, useRef, useEffect } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
  RiNotification3Line,
  RiDeleteBinLine,
  RiCloseLine,
} from "react-icons/ri";

import { notificationService } from "../notificationService";
import {
  isPushMessagingConfigured,
  registerCurrentDeviceForPush,
} from "../firebaseMessagingService";
import { useAuth } from "../../../shared/hooks/useAuth";
import { useAppQuery } from "../../../shared/hooks/useAppQuery";
import { useAppMutation } from "../../../shared/hooks/useAppMutation";
import type {
  NotificationResponse,
  NotificationType,
} from "../notification.types";

// Type metadata
const TYPE_CONFIG: Record<
  NotificationType,
  { icon: string; color: string; bg: string; label: string }
> = {
  BUDGET_EXCEEDED: {
    icon: "⚠",
    color: "text-amber-700",
    bg: "bg-amber-50",
    label: "Budget exceeded",
  },
  BUDGET_WARNING: {
    icon: "!",
    color: "text-amber-700",
    bg: "bg-amber-50",
    label: "Budget warning",
  },
  RECURRING_TRANSACTION_DUE: {
    icon: "↻",
    color: "text-blue-700",
    bg: "bg-blue-50",
    label: "Recurring",
  },
  SAVINGS_GOAL_ACHIEVED: {
    icon: "◎",
    color: "text-green-700",
    bg: "bg-green-50",
    label: "Goal achieved",
  },
  SAVINGS_GOAL_FAILED: {
    icon: "◎",
    color: "text-amber-700",
    bg: "bg-amber-50",
    label: "Goal update",
  },
  PAYMENT_SUCCESS: {
    icon: "✓",
    color: "text-green-700",
    bg: "bg-green-50",
    label: "Payment",
  },
  SECURITY_ALERT: {
    icon: "⚡",
    color: "text-red-700",
    bg: "bg-red-50",
    label: "Security",
  },
  GENERAL: { icon: "·", color: "text-gray-600", bg: "bg-gray-100", label: "Update" },
  SYSTEM: { icon: "·", color: "text-gray-600", bg: "bg-gray-100", label: "System" },
};

// Relative time formatter
const formatRelativeTime = (isoStr: string): string => {
  const diff = Date.now() - new Date(isoStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return "Just now";
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  return `${days}d ago`;
};

const formatAbsoluteTime = (isoStr: string) =>
  new Date(isoStr).toLocaleString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

/* Single notificaiton row */
const NotificationItem: React.FC<{
  notification: NotificationResponse;
  onDelete: (id: string) => void;
  onMarkRead: (id: string) => void;
  isDeleting: boolean;
}> = ({ notification, onDelete, onMarkRead, isDeleting }) => {
  const cfg =
    TYPE_CONFIG[notification.type as NotificationType] ?? TYPE_CONFIG.SYSTEM;

  return (
    <div className={`flex items-start gap-3 px-4 py-3 transition-colors group ${notification.isRead ? "hover:bg-gray-50" : "bg-blue-50/40 hover:bg-blue-50/70"}`}>
      {/* Type icon */}
      <div
        className={`shrink-0 w-7 h-7 rounded-full ${cfg.bg} flex items-center justify-center text-xs font-bold mt-0.5 ${cfg.color}`}
      >
        {cfg.icon}
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        <p className="text-[11px] uppercase tracking-wide text-gray-400 font-semibold">
          {cfg.label}
        </p>
        <p className="text-[13px] text-gray-900 leading-snug mt-0.5 font-medium">
          {notification.title}
        </p>
        <p className="text-[13px] text-gray-800 leading-snug mt-0.5">
          {notification.message}
        </p>
        <p
          className="text-[11px] text-gray-400 mt-1"
          title={formatAbsoluteTime(notification.createdAt)}
        >
          {formatRelativeTime(notification.createdAt)}
        </p>
      </div>

      {/* Delete button — visible on hover */}
      <button
        onClick={() => onDelete(notification.id)}
        disabled={isDeleting}
        className="shrink-0 opacity-0 group-hover:opacity-100 transition-opacity p-1 rounded text-gray-300 hover:text-red-500 disabled:opacity-20"
        aria-label="Dismiss notification"
      >
        <RiCloseLine size={13} />
      </button>
      {!notification.isRead && (
        <button
          onClick={() => onMarkRead(notification.id)}
          className="shrink-0 text-[11px] text-blue-700 hover:text-blue-900"
          aria-label="Mark notification as read"
        >
          Mark read
        </button>
      )}
    </div>
  );
};

/* Main bell component */
interface Props {
  /** Pass "sm" on tablet to render a smaller icon */
  size?: "sm" | "md";
  /** Align the dropdown panel to the left or right of the bell icon */
  align?: "left" | "right";
}

const NotificationBell: React.FC<Props> = ({ size = "md", align = "left" }) => {
  const [open, setOpen] = useState(false);
  const [pushSync, setPushSync] = useState<{
    userId: string;
    status: "enabling" | "enabled" | "error";
    error?: string;
  } | null>(null);
  const panelRef = useRef<HTMLDivElement>(null);
  const queryClient = useQueryClient();
  const { user, isAuthenticated } = useAuth();
  const notificationPermission =
    typeof Notification === "undefined" ? "unsupported" : Notification.permission;
  const pushStatus = !isPushMessagingConfigured
    ? "unavailable"
    : !isAuthenticated || !user?.id || notificationPermission !== "granted"
      ? "disabled"
      : pushSync && pushSync.userId === user.id
        ? pushSync.status
        : "checking";
  const pushError =
    pushSync && pushSync.userId === user?.id ? pushSync.error ?? null : null;

  useEffect(() => {
    if (!isAuthenticated || !user?.id || !isPushMessagingConfigured) {
      return;
    }

    if (typeof Notification === "undefined" || Notification.permission !== "granted") {
      return;
    }

    let isCurrent = true;
    void registerCurrentDeviceForPush(false)
      .then(() => {
        if (isCurrent) {
          setPushSync({ userId: user.id, status: "enabled" });
        }
      })
      .catch((error: unknown) => {
        if (!isCurrent) return;
        setPushSync({
          userId: user.id,
          status: "error",
          error:
            error instanceof Error
              ? error.message
              : "Could not sync push notifications for this account.",
        });
      });

    return () => {
      isCurrent = false;
    };
  }, [isAuthenticated, user?.id]);

  const handleEnablePush = async () => {
    if (!user?.id) return;
    setPushSync({ userId: user.id, status: "enabling" });
    try {
      await registerCurrentDeviceForPush();
      setPushSync({ userId: user.id, status: "enabled" });
    } catch (error: unknown) {
      setPushSync({
        userId: user.id,
        status: "error",
        error:
          error instanceof Error
            ? error.message
            : "Could not enable push notifications.",
      });
    }
  };

  /* Fetch */
  const { data, error, refetch } = useAppQuery({
    queryKey: ["notifications"],
    queryFn: () => notificationService.getAll({ page: 0, size: 20 }),
    refetchInterval: 30_000,
  });

  const { data: unreadData } = useAppQuery({
    queryKey: ["notifications", "unread-count"],
    queryFn: () => notificationService.getUnreadCount(),
    refetchInterval: 30_000,
  });
  const notifications = data?.data?.items ?? [];
  const count = unreadData?.data.unreadCount ?? 0;

  /* Delete one */
  const { mutate: deleteOne, isPending: isDeletingOne } = useAppMutation({
    mutationFn: (id: string) => notificationService.deleteById(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
      queryClient.invalidateQueries({ queryKey: ["notifications", "unread-count"] });
    },
  });

  const { mutate: markOne } = useAppMutation({
    mutationFn: (id: string) => notificationService.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
      queryClient.invalidateQueries({ queryKey: ["notifications", "unread-count"] });
    },
  });

  const { mutate: markAll } = useAppMutation({
    mutationFn: () => notificationService.markAllAsRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
      queryClient.invalidateQueries({ queryKey: ["notifications", "unread-count"] });
    },
  });

  /* Delete all */
  const { mutate: deleteAll, isPending: isDeletingAll } = useAppMutation<
    void,
    void
  >({
    mutationFn: () => notificationService.deleteAll(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  /* Close on outside click */
  useEffect(() => {
    if (!open) return;
    const handle = (e: MouseEvent) => {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handle);
    return () => document.removeEventListener("mousedown", handle);
  }, [open]);

  const iconSize = size === "sm" ? 17 : 19;

  return (
    <div className="relative" ref={panelRef}>
      {/* Bell button */}
      <button
        onClick={() => setOpen((v) => !v)}
        className={`relative flex items-center justify-center rounded-lg transition-colors
          ${size === "sm" ? "w-10 h-10" : "p-1.5"}
          text-gray-400 hover:text-gray-700 hover:bg-gray-50`}
        aria-label="Notifications"
      >
        <RiNotification3Line size={iconSize} />

        {/* Badge */}
        {count > 0 && (
          <span className="absolute -top-0.5 -right-0.5 min-w-[16px] h-4 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center px-1 leading-none">
            {count > 9 ? "9+" : count}
          </span>
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div
          className={`absolute ${align === "right" ? "right-0" : "left-0"} top-full mt-2 w-80 max-w-[calc(100vw-2rem)] bg-white border border-gray-200 rounded-xl shadow-lg z-50 overflow-hidden`}
          style={{ maxHeight: "420px" }}
        >
          {/* Panel header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
            <div className="flex items-center gap-2">
              <span className="text-sm font-semibold text-gray-900">
                Notifications
              </span>
              {count > 0 && (
                <span className="text-xs bg-gray-100 text-gray-600 px-1.5 py-0.5 rounded-full font-medium">
                  {count}
                </span>
              )}
            </div>
              <div className="flex items-center gap-3">
                {count > 0 && (
                  <button
                    onClick={() => markAll()}
                    className="text-xs text-blue-700 hover:text-blue-900"
                  >
                    Mark all read
                  </button>
                )}
                {notifications.length > 0 && (
                  <button
                    onClick={() => deleteAll()}
                    disabled={isDeletingAll}
                    className="flex items-center gap-1 text-xs text-gray-400 hover:text-red-500 transition-colors disabled:opacity-40"
                  >
                    <RiDeleteBinLine size={12} />
                    Clear all
                  </button>
                )}
              </div>
          </div>

          <div className="border-b border-gray-100 px-4 py-3">
            <div className="flex items-center justify-between gap-3">
              <div>
                <p className="text-sm font-medium text-gray-800">
                  Push notifications
                </p>
                <p className="mt-0.5 text-xs text-gray-500">
                  {pushStatus === "enabled"
                    ? "Enabled on this device"
                    : pushStatus === "checking"
                      ? "Checking this device..."
                      : pushStatus === "enabling"
                        ? "Enabling notifications..."
                        : pushStatus === "error"
                          ? "Could not sync this device"
                          : "Get important updates on this device."}
                </p>
              </div>
              {(pushStatus === "disabled" ||
                (pushStatus === "error" && notificationPermission !== "denied")) && (
                <button
                  type="button"
                  onClick={handleEnablePush}
                  className="shrink-0 rounded-lg bg-blue-600 px-3 py-2 text-xs font-medium text-white hover:bg-blue-700"
                >
                  {pushStatus === "error" &&
                  notificationPermission === "granted"
                    ? "Retry"
                    : "Enable"}
                </button>
              )}
            </div>
            {pushStatus === "unavailable" && (
              <p className="mt-2 text-xs text-amber-700">
                Push notifications are not configured for this app.
              </p>
            )}
            {notificationPermission === "denied" &&
              pushStatus !== "enabled" && (
                <p className="mt-2 text-xs text-amber-700">
                  Notifications are blocked in browser settings. Allow them for
                  this site to enable push.
                </p>
              )}
            {pushError && (
              <p
                role="alert"
                className="mt-2 text-xs text-red-700"
              >
                {pushError}
              </p>
            )}
          </div>

          {/* Notification list */}
          <div className="overflow-y-auto" style={{ maxHeight: "360px" }}>
            {error ? (
              <div className="px-4 py-8 text-center">
                <p className="text-sm text-gray-600">Notifications couldn't be loaded.</p>
                <button onClick={() => refetch()} className="mt-2 text-xs text-blue-700">Try again</button>
              </div>
            ) : notifications.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-10 px-4 text-center">
                <div className="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center mb-3">
                  <RiNotification3Line size={18} className="text-gray-400" />
                </div>
                <p className="text-sm text-gray-400">You're all caught up</p>
                <p className="text-xs text-gray-300 mt-1">
                  Budget alerts and goal updates will appear here
                </p>
              </div>
            ) : (
              <div className="divide-y divide-gray-50">
                {notifications.map((n) => (
                  <NotificationItem
                    key={n.id}
                    notification={n}
                    onDelete={deleteOne}
                    onMarkRead={markOne}
                    isDeleting={isDeletingOne}
                  />
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
