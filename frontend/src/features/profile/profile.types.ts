export interface UserProfileResponse {
  id: string;
  fullName: string;
  email: string;
  status: 'INACTIVE' | 'ACTIVE' | 'PENDING_VERIFICATION' | 'DELETED';
}

export interface UpdateProfileRequest {
  fullName: string;
}

export interface DeleteAccountRequest {
  password: string;
}
