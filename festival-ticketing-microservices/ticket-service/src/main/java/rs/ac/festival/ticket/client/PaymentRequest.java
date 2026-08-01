package rs.ac.festival.ticket.client;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(UUID ticketId, Long userId, Long festivalId, String email, BigDecimal amount) {
}
