package rs.ac.festival.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import rs.ac.festival.user.api.CreateUserRequest;
import rs.ac.festival.user.api.LoginRequest;
import rs.ac.festival.user.domain.Role;
import rs.ac.festival.user.service.UserAccountService;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "eureka.client.enabled=false")
@Transactional
class UserServiceIntegrationTest {
    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.4"))
        .withDatabaseName("userdb")
        .withUsername("festival")
        .withPassword("festivalpass");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired UserAccountService service;

    @Test
    void persistsAndAuthenticatesUserAgainstRealMySql() {
        var created = service.create(new CreateUserRequest(
            "integration@example.com", "integration-secret", "Integration User", Set.of(Role.CUSTOMER)
        ));

        var authenticated = service.authenticate(new LoginRequest("integration@example.com", "integration-secret"));

        assertThat(created.email()).isEqualTo("integration@example.com");
        assertThat(authenticated.id()).isEqualTo(created.id());
    }
}
