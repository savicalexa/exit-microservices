package rs.ac.festival.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rs.ac.festival.notification.messaging.PaymentCompletedEvent;

@Service
public class EmailNotificationService {
    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendPaymentConfirmation(PaymentCompletedEvent event) {
        log.info(
            "SIMULATED_EMAIL to={} subject='Festival ticket payment confirmed' " +
            "paymentId={} ticketId={} festivalId={} amount={}",
            event.email(), event.paymentId(), event.ticketId(), event.festivalId(), event.amount()
        );
    }
}
