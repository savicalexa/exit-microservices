package rs.ac.festival.notification.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
    UUID eventId, String paymentId, UUID ticketId, Long userId, Long festivalId,
    String email, BigDecimal amount, String status, Instant occurredAt
) {
}
