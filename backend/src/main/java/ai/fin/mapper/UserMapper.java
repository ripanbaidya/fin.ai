package ai.fin.mapper;

import ai.fin.dto.user.UserProfileDetails;
import ai.fin.entities.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserProfileDetails toResponse(User user) {
        return new UserProfileDetails(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getAccountStatus().name()
        );
    }
}
