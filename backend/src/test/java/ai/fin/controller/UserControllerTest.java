package ai.fin.controller;

import ai.fin.dto.auth.DeleteAccountRequest;
import ai.fin.dto.auth.LogoutRequest;
import ai.fin.security.UserPrincipal;
import ai.fin.service.AuthService;
import ai.fin.shared.exception.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;

    private UserPrincipal testPrincipal;

    @BeforeEach
    void setUp() {
        testPrincipal = new UserPrincipal(
                "usr-123",
                "test@walletiq.ai",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                true
        );

        // HandlerMethodArgumentResolver to resolve @AuthenticationPrincipal in standalone MockMvc
        HandlerMethodArgumentResolver principalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return testPrincipal;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(principalResolver)
                .build();
    }

    @Test
    @DisplayName("POST /users/logout should successfully log out user and return 204")
    void shouldLogoutUser() throws Exception {
        LogoutRequest request = new LogoutRequest("refresh-token-xyz");

        mockMvc.perform(post("/users/logout")
                        .header("Authorization", "Bearer access-token-abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("Logout Successful"));

        verify(authService).logout("access-token-abc", "refresh-token-xyz");
    }

    @Test
    @DisplayName("DELETE /users/me should pass principal userId to authService.deleteAccount")
    void shouldDeleteAccountUsingPrincipalUserId() throws Exception {
        DeleteAccountRequest request = new DeleteAccountRequest("ValidPassword123!");

        mockMvc.perform(delete("/users/me")
                        .header("Authorization", "Bearer access-token-abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.code").value("DELETED"));

        verify(authService).deleteAccount("usr-123", "ValidPassword123!", "access-token-abc");
    }
}
