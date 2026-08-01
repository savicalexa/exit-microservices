package rs.ac.festival.ticket.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.festival.ticket.api.ReservationRequest;
import rs.ac.festival.ticket.client.EventClient;
import rs.ac.festival.ticket.client.FestivalSummary;
import rs.ac.festival.ticket.client.PaymentClient;
import rs.ac.festival.ticket.client.UserClient;
import rs.ac.festival.ticket.client.UserSummary;
import rs.ac.festival.ticket.domain.TicketInventory;
import rs.ac.festival.ticket.domain.TicketReservation;
import rs.ac.festival.ticket.repository.TicketInventoryRepository;
import rs.ac.festival.ticket.repository.TicketReservationRepository;
import rs.ac.festival.ticket.support.ConflictException;

@ExtendWith(MockitoExtension.class)
class TicketReservationServiceTest {
    @Mock TicketReservationRepository ticketRepository;
    @Mock TicketInventoryRepository inventoryRepository;
    @Mock UserClient userClient;
    @Mock EventClient eventClient;
    @Mock PaymentClient paymentClient;
    TicketReservationService service;

    @BeforeEach
    void setUp() {
        service = new TicketReservationService(
            ticketRepository, inventoryRepository, userClient, eventClient, paymentClient, Duration.ofMinutes(15)
        );
    }

    @Test
    void reservesTicketAfterSynchronousValidation() {
        var inventory = org.mockito.Mockito.mock(TicketInventory.class);
        when(userClient.getUser(1L)).thenReturn(new UserSummary(1L, "user@example.com", "User", Set.of("CUSTOMER"), true));
        when(eventClient.getFestival(2L)).thenReturn(new FestivalSummary(2L, "Exit", "Novi Sad", 100));
        when(inventoryRepository.findForUpdate(2L)).thenReturn(Optional.of(inventory));
        when(inventory.getActiveTickets()).thenReturn(10);
        when(ticketRepository.save(any(TicketReservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.reserve(new ReservationRequest(1L, 2L, new BigDecimal("49.99")));

        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.festivalId()).isEqualTo(2L);
        assertThat(result.festivalName()).isEqualTo("Exit");
    }

    @Test
    void rejectsInactiveUser() {
        when(userClient.getUser(1L)).thenReturn(new UserSummary(1L, "user@example.com", "User", Set.of(), false));
        assertThatThrownBy(() -> service.reserve(new ReservationRequest(1L, 2L, BigDecimal.TEN)))
            .isInstanceOf(ConflictException.class)
            .hasMessageContaining("inactive");
    }
}
