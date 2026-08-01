package rs.ac.festival.user.api;

import java.util.Set;
import rs.ac.festival.user.domain.Role;
import rs.ac.festival.user.domain.UserAccount;

public record UserResponse(Long id, String email, String ime, Set<Role> roles, boolean active) {
    public static UserResponse from(UserAccount user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getIme(), user.getRoles(), user.isActive());
    }
}
