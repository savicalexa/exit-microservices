package rs.ac.festival.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import rs.ac.festival.ticket.api.ReservationRequest;
import rs.ac.festival.ticket.client.EventClient;
import rs.ac.festival.ticket.client.FestivalSummary;
import rs.ac.festival.ticket.client.PaymentClient;
import rs.ac.festival.ticket.client.UserClient;
import rs.ac.festival.ticket.client.UserSummary;
import rs.ac.festival.ticket.domain.TicketStatus;
import rs.ac.festival.ticket.service.TicketReservationService;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
    "eureka.client.enabled=false",
    "spring.rabbitmq.dynamic=false",
    "spring.rabbitmq.listener.simple.auto-startup=false",
    "spring.task.scheduling.enabled=false"
})
@Transactional
class TicketServiceIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"))
        .withDatabaseName("ticketdb")
        .withUsername("festival")
        .withPassword("festivalpass");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @MockitoBean UserClient userClient;
    @MockitoBean EventClient eventClient;
    @MockitoBean PaymentClient paymentClient;
    @Autowired TicketReservationService service;

    @Test
    void reservationIsPersistedInRealPostgres() {
        when(userClient.getUser(1L)).thenReturn(new UserSummary(1L, "integration@example.com", "User", Set.of("CUSTOMER"), true));
        when(eventClient.getFestival(2L)).thenReturn(new FestivalSummary(2L, "Test Festival", "Novi Sad", 2));

        var reserved = service.reserve(new ReservationRequest(1L, 2L, new BigDecimal("20.00")));
        var loaded = service.get(reserved.id());

        assertThat(loaded.status()).isEqualTo(TicketStatus.RESERVED);
        assertThat(loaded.amount()).isEqualByComparingTo("20.00");
    }
}
