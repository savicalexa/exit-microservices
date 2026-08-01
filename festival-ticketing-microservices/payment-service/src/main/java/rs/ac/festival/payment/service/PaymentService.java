package rs.ac.festival.payment.service;

import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import rs.ac.festival.payment.api.PaymentRequest;
import rs.ac.festival.payment.api.PaymentResponse;
import rs.ac.festival.payment.domain.Payment;
import rs.ac.festival.payment.messaging.PaymentCompletedEvent;
import rs.ac.festival.payment.messaging.RabbitConfiguration;
import rs.ac.festival.payment.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public PaymentService(PaymentRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public PaymentResponse process(PaymentRequest request) {
        var existing = repository.findByTicketId(request.ticketId().toString());
        if (existing.isPresent()) return PaymentResponse.from(existing.get());

        Payment payment;
        try {
            payment = repository.save(new Payment(
                request.ticketId(), request.userId(), request.festivalId(), request.email(), request.amount()
            ));
        } catch (DuplicateKeyException concurrentRequest) {
            payment = repository.findByTicketId(request.ticketId().toString()).orElseThrow(() -> concurrentRequest);
            return PaymentResponse.from(payment);
        }

        PaymentCompletedEvent event = new PaymentCompletedEvent(
            UUID.randomUUID(), payment.getId(), request.ticketId(), request.userId(), request.festivalId(),
            request.email(), request.amount(), payment.getStatus().name(), payment.getCreatedAt()
        );
        rabbitTemplate.convertAndSend(
            RabbitConfiguration.EXCHANGE, RabbitConfiguration.PAYMENT_COMPLETED_KEY, event,
            message -> { message.getMessageProperties().setMessageId(event.eventId().toString()); return message; }
        );
        return PaymentResponse.from(payment);
    }

    public PaymentResponse get(String id) {
        return repository.findById(id).map(PaymentResponse::from)
            .orElseThrow(() -> new PaymentNotFoundException("Payment " + id + " was not found"));
    }
}
