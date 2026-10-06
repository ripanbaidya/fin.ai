import { apiClient } from "../../lib/axios";
import type {
  DeviceTokenRemovalRequest,
  DeviceTokenRequest,
} from "./notification.types";

export const deviceTokenService = {
  register: async (data: DeviceTokenRequest): Promise<void> => {
    await apiClient.post<void>("/devices/tokens", data);
  },

  unregister: async (data: DeviceTokenRemovalRequest): Promise<void> => {
    await apiClient.delete<void>("/devices/tokens", { data });
  },
};
