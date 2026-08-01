package rs.ac.festival.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import rs.ac.festival.payment.api.PaymentRequest;
import rs.ac.festival.payment.domain.Payment;
import rs.ac.festival.payment.domain.PaymentStatus;
import rs.ac.festival.payment.messaging.PaymentCompletedEvent;
import rs.ac.festival.payment.messaging.RabbitConfiguration;
import rs.ac.festival.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock PaymentRepository repository;
    @Mock RabbitTemplate rabbitTemplate;
    @InjectMocks PaymentService service;

    @Test
    void storesPaymentAndPublishesCompletionEvent() {
        UUID ticketId = UUID.randomUUID();
        PaymentRequest request = new PaymentRequest(ticketId, 1L, 2L, "user@example.com", new BigDecimal("30.00"));
        when(repository.findByTicketId(ticketId.toString())).thenReturn(Optional.empty());
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.process(request);

        assertThat(response.status()).isEqualTo(PaymentStatus.SUCCEEDED);
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfiguration.EXCHANGE), eq(RabbitConfiguration.PAYMENT_COMPLETED_KEY),
            any(PaymentCompletedEvent.class), any(MessagePostProcessor.class)
        );
    }

    @Test
    void isIdempotentForSameTicket() {
        UUID ticketId = UUID.randomUUID();
        Payment existing = new Payment(ticketId, 1L, 2L, "user@example.com", BigDecimal.TEN);
        when(repository.findByTicketId(ticketId.toString())).thenReturn(Optional.of(existing));

        var response = service.process(new PaymentRequest(ticketId, 1L, 2L, "user@example.com", BigDecimal.TEN));

        assertThat(response.id()).isEqualTo(existing.getId());
    }
}
