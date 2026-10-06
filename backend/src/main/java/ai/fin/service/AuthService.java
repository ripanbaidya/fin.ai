package ai.fin.service;

import ai.fin.dto.auth.AuthResponse;
import ai.fin.dto.auth.LoginRequest;
import ai.fin.dto.auth.TokenResponse;
import ai.fin.dto.auth.UserRegisterRequest;

public interface AuthService {

    AuthResponse registerUser(UserRegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    void deleteAccount(String userId, String password, String accessToken);

}