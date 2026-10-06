export type NotificationType =
  | "BUDGET_EXCEEDED"
  | "BUDGET_WARNING"
  | "SAVINGS_GOAL_ACHIEVED"
  | "SAVINGS_GOAL_FAILED"
  | "RECURRING_TRANSACTION_DUE"
  | "PAYMENT_SUCCESS"
  | "SECURITY_ALERT"
  | "GENERAL"
  | "SYSTEM";

export interface NotificationResponse {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  readAt: string | null;
  data: Record<string, string>;
  createdAt: string;
  updatedAt: string;
}

export interface UnreadNotificationCountResponse {
  unreadCount: number;
}

export interface DeviceTokenRequest {
  token: string;
  platform: "WEB";
}

export interface DeviceTokenRemovalRequest {
  token: string;
}
