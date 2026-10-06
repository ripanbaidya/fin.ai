export interface UserProfileResponse {
  id: string;
  fullName: string;
  email: string;
  status: AccountStatus;
}

export type Role = 'USER' | 'ADMIN';
export type AccountStatus = 'INACTIVE' | 'ACTIVE' | 'PENDING_VERIFICATION' | 'DELETED';