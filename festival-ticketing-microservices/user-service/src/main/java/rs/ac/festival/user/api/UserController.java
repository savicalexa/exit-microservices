package rs.ac.festival.user.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.user.service.UserAccountService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserAccountService service;
    public UserController(UserAccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @PostMapping("/authenticate")
    public UserResponse authenticate(@Valid @RequestBody LoginRequest request) { return service.authenticate(request); }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public List<UserResponse> list() { return service.list(); }

    @PatchMapping("/{id}/active")
    public UserResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return service.setActive(id, value);
    }

    @PatchMapping("/{id}/roles")
    public UserResponse updateRoles(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return service.updateRoles(id, request.roles());
    }
}
