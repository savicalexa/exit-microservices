package rs.ac.festival.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import rs.ac.festival.user.domain.Role;

public record CreateUserRequest(
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 10, max = 72) String password,
    @NotBlank @Size(max = 120) String ime,
    Set<Role> roles
) {
    public Set<Role> normalizedRoles() {
        return roles == null || roles.isEmpty() ? Set.of(Role.CUSTOMER) : Set.copyOf(roles);
    }
}
