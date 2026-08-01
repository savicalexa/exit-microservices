package rs.ac.festival.ticket.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.festival.ticket.service.TicketReservationService;

@Component
public class PaymentEventListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);
    private final TicketReservationService service;

    public PaymentEventListener(TicketReservationService service) { this.service = service; }

    @RabbitListener(queues = RabbitConfiguration.TICKET_QUEUE)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        if (!"SUCCEEDED".equals(event.status())) {
            log.warn("Ignoring non-successful payment event {} with status {}", event.eventId(), event.status());
            return;
        }
        service.markPurchased(event.ticketId(), event.occurredAt());
        log.info("Ticket {} marked as purchased from payment event {}", event.ticketId(), event.eventId());
    }
}
