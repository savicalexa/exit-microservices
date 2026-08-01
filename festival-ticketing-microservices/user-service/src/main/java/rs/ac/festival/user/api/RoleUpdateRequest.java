package rs.ac.festival.user.api;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import rs.ac.festival.user.domain.Role;

public record RoleUpdateRequest(@NotEmpty Set<Role> roles) {
}
