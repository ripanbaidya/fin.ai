package ai.fin.service.impl;

import ai.fin.dto.user.UpdateProfileRequest;
import ai.fin.dto.user.UserProfileDetails;
import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("updateProfile should save and return the trimmed full name")
    void shouldUpdateProfileFullName() {
        User user = new User();
        user.setId("user-123");
        user.setFullName("Old Name");
        user.setEmail("test@walletiq.ai");
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.getReferenceById("user-123")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileDetails result = userService.updateProfile(
                "user-123",
                new UpdateProfileRequest("  New Name  ")
        );

        assertThat(user.getFullName()).isEqualTo("New Name");
        assertThat(result.fullName()).isEqualTo("New Name");
        verify(userRepository).save(user);
    }
}
