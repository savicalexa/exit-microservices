package rs.ac.festival.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.user.service.UserAccountService;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    private final UserAccountService service;
    public InternalUserController(UserAccountService service) { this.service = service; }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) { return service.get(id); }
}
