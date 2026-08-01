package rs.ac.festival.ticket.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import rs.ac.festival.ticket.domain.TicketReservation;
import rs.ac.festival.ticket.domain.TicketStatus;

public record TicketResponse(
    UUID id, Long userId, Long festivalId, String festivalName, BigDecimal amount,
    TicketStatus status, Instant createdAt, Instant expiresAt, Instant paidAt
) {
    public static TicketResponse from(TicketReservation ticket) {
        return new TicketResponse(
            ticket.getId(), ticket.getUserId(), ticket.getFestivalId(), ticket.getFestivalName(),
            ticket.getAmount(), ticket.getStatus(), ticket.getCreatedAt(), ticket.getExpiresAt(), ticket.getPaidAt()
        );
    }
}
