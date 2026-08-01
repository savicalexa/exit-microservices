package rs.ac.festival.payment.api;

import java.math.BigDecimal;
import java.time.Instant;
import rs.ac.festival.payment.domain.Payment;
import rs.ac.festival.payment.domain.PaymentStatus;

public record PaymentResponse(
    String id, String ticketId, BigDecimal amount, PaymentStatus status, Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(), payment.getTicketId(), payment.getAmount(), payment.getStatus(), payment.getCreatedAt()
        );
    }
}
