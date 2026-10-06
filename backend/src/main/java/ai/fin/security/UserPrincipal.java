package ai.fin.security;

import ai.fin.entities.User;
import ai.fin.enums.AccountStatus;
import ai.fin.security.jwt.JwtClaimNames;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents the principal for authentication and authorization using the
 * {@link UserDetails} interface. Encapsulates user-specific information such as
 * user ID, email, password, granted authorities, and account status.
 */
@Getter
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public UserPrincipal(String id,
                         String email,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         boolean enabled) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    /**
     * Factory method to create a UserPrincipal from a User entity (e.g. during database authentication).
     */
    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole().getAuthority())),
                user.getAccountStatus() != AccountStatus.DELETED
        );
    }

    /**
     * Factory method to create a UserPrincipal directly from cryptographically validated JWT claims (zero database lookup).
     */
    public static UserPrincipal fromClaims(Claims claims) {
        String userId = claims.getSubject();
        String email = claims.get(JwtClaimNames.EMAIL, String.class);
        String role = claims.get(JwtClaimNames.ROLE, String.class);
        String status = claims.get(JwtClaimNames.STATUS, String.class);

        String authority = (role != null && role.startsWith("ROLE_")) ? role : "ROLE_" + role;
        boolean enabled = !AccountStatus.DELETED.name().equalsIgnoreCase(status);

        return new UserPrincipal(
                userId,
                email,
                null,
                List.of(new SimpleGrantedAuthority(authority)),
                enabled
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
