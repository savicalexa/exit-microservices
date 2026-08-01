package rs.ac.festival.user.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.festival.user.api.CreateUserRequest;
import rs.ac.festival.user.api.LoginRequest;
import rs.ac.festival.user.api.UserResponse;
import rs.ac.festival.user.domain.UserAccount;
import rs.ac.festival.user.repository.UserAccountRepository;
import rs.ac.festival.user.support.ConflictException;
import rs.ac.festival.user.support.ResourceNotFoundException;
import rs.ac.festival.user.support.UnauthorizedException;

@Service
public class UserAccountService {
    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (repository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        UserAccount user = new UserAccount(
            request.email(), passwordEncoder.encode(request.password()), request.ime(), request.normalizedRoles()
        );
        return UserResponse.from(repository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse get(Long id) { return UserResponse.from(find(id)); }

    @Transactional(readOnly = true)
    public List<UserResponse> list() { return repository.findAll().stream().map(UserResponse::from).toList(); }

    @Transactional(readOnly = true)
    public UserResponse authenticate(LoginRequest request) {
        UserAccount user = repository.findByEmailIgnoreCase(request.email())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse setActive(Long id, boolean active) {
        UserAccount user = find(id);
        if (active) user.activate(); else user.deactivate();
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateRoles(Long id, java.util.Set<rs.ac.festival.user.domain.Role> roles) {
        UserAccount user = find(id);
        user.changeRoles(roles);
        return UserResponse.from(user);
    }

    private UserAccount find(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User " + id + " was not found"));
    }
}
