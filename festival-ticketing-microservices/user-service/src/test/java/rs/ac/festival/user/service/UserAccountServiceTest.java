package rs.ac.festival.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.ac.festival.user.api.CreateUserRequest;
import rs.ac.festival.user.domain.Role;
import rs.ac.festival.user.domain.UserAccount;
import rs.ac.festival.user.repository.UserAccountRepository;
import rs.ac.festival.user.support.ConflictException;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {
    @Mock UserAccountRepository repository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserAccountService service;

    @Test
    void hashesPasswordAndCreatesCustomer() {
        var request = new CreateUserRequest("USER@example.com", "very-secret", "Ana", Set.of(Role.CUSTOMER));
        when(repository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        when(passwordEncoder.encode("very-secret")).thenReturn("hash");
        when(repository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.roles()).containsExactly(Role.CUSTOMER);
    }

    @Test
    void rejectsDuplicateEmail() {
        var request = new CreateUserRequest("user@example.com", "very-secret", "Ana", Set.of(Role.CUSTOMER));
        when(repository.existsByEmailIgnoreCase(request.email())).thenReturn(true);
        assertThatThrownBy(() -> service.create(request)).isInstanceOf(ConflictException.class);
    }
}
