package ai.fin.service;

import ai.fin.dto.user.UpdateProfileRequest;
import ai.fin.dto.user.UserProfileDetails;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.shared.api.PaginatedData;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserProfileDetails getProfile(String userId);

    UserProfileDetails updateProfile(String userId, UpdateProfileRequest request);
    // Admin specific

    PaginatedData<UserProfileDetails> getAllUsers(String adminId, Pageable pageable);

    long countTotalUsers(String adminId, Role role, AccountStatus accountStatus);
}
