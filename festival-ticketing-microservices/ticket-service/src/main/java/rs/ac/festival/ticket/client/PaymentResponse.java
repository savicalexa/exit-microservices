package rs.ac.festival.ticket.client;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(String id, String ticketId, BigDecimal amount, String status, Instant createdAt) {
}
