package ai.fin.service.impl;

import ai.fin.dto.user.UpdateProfileRequest;
import ai.fin.dto.user.UserProfileDetails;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.mapper.UserMapper;
import ai.fin.repository.UserRepository;
import ai.fin.service.UserService;
import ai.fin.shared.api.PaginatedData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDetails getProfile(String userId) {
        return UserMapper.toResponse(userRepository.getReferenceById(userId));
    }

    @Override
    @Transactional
    public UserProfileDetails updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.getReferenceById(userId);
        if (request.fullName() != null && !request.fullName().trim().equals(user.getFullName())) {
            user.setFullName(request.fullName().trim());
        }
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<UserProfileDetails> getAllUsers(String adminId, Pageable pageable) {
        Page<UserProfileDetails> mapped = userRepository.findAll(pageable).map(UserMapper::toResponse);
        return PaginatedData.from(mapped);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalUsers(String adminId, Role role, AccountStatus accountStatus) {
        return userRepository.countByRoleAndAccountStatus(role, accountStatus);
    }
}
