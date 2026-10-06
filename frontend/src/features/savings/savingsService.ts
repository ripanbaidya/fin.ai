import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type {
  GoalProgressResponse,
  CreateSavingsGoalRequest,
  ContributeRequest,
} from './savings.types';

export const savingsService = {
  getAll: async (): Promise<ResponseWrapper<GoalProgressResponse[]>> => {
    const res = await apiClient.get<ResponseWrapper<GoalProgressResponse[]>>('/savings-goals');
    return res.data;
  },

  create: async (data: CreateSavingsGoalRequest): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.post<ResponseWrapper<GoalProgressResponse>>('/savings-goals', data);
    return res.data;
  },

  contribute: async (id: string, data: ContributeRequest): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.patch<ResponseWrapper<GoalProgressResponse>>(`/savings-goals/${id}/contribute`, data);
    return res.data;
  },

  getProgress: async (id: string): Promise<ResponseWrapper<GoalProgressResponse>> => {
    const res = await apiClient.get<ResponseWrapper<GoalProgressResponse>>(`/savings-goals/${id}/progress`);
    return res.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete<void>(`/savings-goals/${id}`);
  },
};
