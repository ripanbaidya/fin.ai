package ai.fin.security;

import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.enums.Role;
import ai.fin.security.jwt.JwtClaimNames;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserPrincipalTest {

    @Test
    @DisplayName("Should create UserPrincipal from User entity with correct properties")
    void shouldCreateUserPrincipalFromUser() {
        User user = new User();
        user.setId("usr-12345");
        user.setEmail("user@example.com");
        user.setPasswordHash("hashed-pw");
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);

        UserPrincipal principal = UserPrincipal.from(user);

        assertThat(principal.getId()).isEqualTo("usr-12345");
        assertThat(principal.getEmail()).isEqualTo("user@example.com");
        assertThat(principal.getUsername()).isEqualTo("user@example.com");
        assertThat(principal.getPassword()).isEqualTo("hashed-pw");
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should create UserPrincipal from JWT Claims with null password and zero DB overhead")
    void shouldCreateUserPrincipalFromClaims() {
        Claims claims = Mockito.mock(Claims.class);
        when(claims.getSubject()).thenReturn("usr-12345");
        when(claims.get(JwtClaimNames.EMAIL, String.class)).thenReturn("user@example.com");
        when(claims.get(JwtClaimNames.ROLE, String.class)).thenReturn("USER");
        when(claims.get(JwtClaimNames.STATUS, String.class)).thenReturn("ACTIVE");

        UserPrincipal principal = UserPrincipal.fromClaims(claims);

        assertThat(principal.getId()).isEqualTo("usr-12345");
        assertThat(principal.getEmail()).isEqualTo("user@example.com");
        assertThat(principal.getUsername()).isEqualTo("user@example.com");
        assertThat(principal.getPassword()).isNull();
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("Should disable UserPrincipal when user account status is DELETED")
    void shouldDisableUserPrincipalWhenStatusIsDeleted() {
        Claims claims = Mockito.mock(Claims.class);
        when(claims.getSubject()).thenReturn("usr-999");
        when(claims.get(JwtClaimNames.EMAIL, String.class)).thenReturn("deleted@example.com");
        when(claims.get(JwtClaimNames.ROLE, String.class)).thenReturn("USER");
        when(claims.get(JwtClaimNames.STATUS, String.class)).thenReturn("DELETED");

        UserPrincipal principal = UserPrincipal.fromClaims(claims);

        assertThat(principal.isEnabled()).isFalse();
    }
}
