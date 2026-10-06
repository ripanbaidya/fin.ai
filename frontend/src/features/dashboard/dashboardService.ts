import { apiClient } from "../../lib/axios";
import type { ResponseWrapper } from "../../types/api.types";
import type { DashboardResponse } from "./dashboard.types";

export const dashboardService = {
  getDashboardData: async (month?: string): Promise<ResponseWrapper<DashboardResponse>> => {
    const res = await apiClient.get<ResponseWrapper<DashboardResponse>>(`/dashboard`, {params: {month}});
    return res.data;
  }
}