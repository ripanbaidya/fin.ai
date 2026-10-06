import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type { UserProfileResponse, Role, AccountStatus } from './admin.types';
import type { PaginatedData } from '../../types/api.types';

export const adminService = {
  getUsers: async (page = 0, size = 20, direction: 'ASC' | 'DESC' = 'DESC'): Promise<ResponseWrapper<PaginatedData<UserProfileResponse>>> => {
    const res = await apiClient.get<ResponseWrapper<PaginatedData<UserProfileResponse>>>(
      '/admin/users',
      { params: { page, size, direction } }
    );
    return res.data;
  },

  getUserById: async (id: string): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.get<ResponseWrapper<UserProfileResponse>>(`/admin/users/${id}`);
    return res.data;
  },

  getUserCount: async (role: Role, accountStatus: AccountStatus): Promise<ResponseWrapper<number>> => {
    const res = await apiClient.get<ResponseWrapper<number>>(
      '/admin/users/count',
      { params: { role, accountStatus } }
    );
    return res.data;
  },
};