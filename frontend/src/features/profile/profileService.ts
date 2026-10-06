import { apiClient } from '../../lib/axios';
import type { ResponseWrapper } from '../../types/api.types';
import type { DeleteAccountRequest, UserProfileResponse, UpdateProfileRequest } from './profile.types';

export const userService = {
  getProfile: async (): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.get<ResponseWrapper<UserProfileResponse>>('/users/me');
    return res.data;
  },

  updateProfile: async (data: UpdateProfileRequest): Promise<ResponseWrapper<UserProfileResponse>> => {
    const res = await apiClient.patch<ResponseWrapper<UserProfileResponse>>('/users/me', data);
    return res.data;
  },

  deleteAccount: async (data: DeleteAccountRequest): Promise<void> => {
    await apiClient.delete<void>('/users/me', { data });
  },
};
