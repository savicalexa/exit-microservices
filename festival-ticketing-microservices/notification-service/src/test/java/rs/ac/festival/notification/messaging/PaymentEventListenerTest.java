package rs.ac.festival.notification.messaging;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.festival.notification.service.EmailNotificationService;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {
    @Mock EmailNotificationService emailService;
    @InjectMocks PaymentEventListener listener;

    @Test
    void sendsConfirmationForSuccessfulPayment() {
        PaymentCompletedEvent event = event("SUCCEEDED");
        listener.onPaymentCompleted(event);
        verify(emailService).sendPaymentConfirmation(event);
    }

    @Test
    void ignoresFailedPayment() {
        PaymentCompletedEvent event = event("FAILED");
        listener.onPaymentCompleted(event);
        verify(emailService, never()).sendPaymentConfirmation(event);
    }

    private PaymentCompletedEvent event(String status) {
        return new PaymentCompletedEvent(
            UUID.randomUUID(), UUID.randomUUID().toString(), UUID.randomUUID(), 1L, 2L,
            "user@example.com", BigDecimal.TEN, status, Instant.now()
        );
    }
}
