import { apiClient } from "../../lib/axios";
import type { PaginatedData, ResponseWrapper } from "../../types/api.types";
import type {
  NotificationResponse,
  NotificationType,
  UnreadNotificationCountResponse,
} from "./notification.types";

export const notificationService = {
  getAll: async (params: {
    unreadOnly?: boolean;
    type?: NotificationType;
    page?: number;
    size?: number;
    direction?: "ASC" | "DESC";
  } = {}): Promise<ResponseWrapper<PaginatedData<NotificationResponse>>> => {
    const res = await apiClient.get<ResponseWrapper<PaginatedData<NotificationResponse>>>(
      "/notifications",
      { params },
    );
    return res.data;
  },

  getUnreadCount: async (): Promise<ResponseWrapper<UnreadNotificationCountResponse>> => {
    const res = await apiClient.get<ResponseWrapper<UnreadNotificationCountResponse>>(
      "/notifications/unread-count",
    );
    return res.data;
  },

  markAsRead: async (id: string): Promise<ResponseWrapper<NotificationResponse>> => {
    const res = await apiClient.patch<ResponseWrapper<NotificationResponse>>(
      `/notifications/${id}/read`,
    );
    return res.data;
  },

  markAllAsRead: async (): Promise<void> => {
    await apiClient.patch("/notifications/read-all");
  },

  deleteById: async (id: string): Promise<void> => {
    await apiClient.delete(`/notifications/${id}`);
  },

  deleteAll: async (readOnly = false): Promise<void> => {
    await apiClient.delete("/notifications", { params: { readOnly } });
  },
};