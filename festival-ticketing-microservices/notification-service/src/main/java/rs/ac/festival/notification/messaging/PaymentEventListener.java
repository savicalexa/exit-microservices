package rs.ac.festival.notification.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import rs.ac.festival.notification.service.EmailNotificationService;

@Component
public class PaymentEventListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);
    private final EmailNotificationService emailService;

    public PaymentEventListener(EmailNotificationService emailService) { this.emailService = emailService; }

    @RabbitListener(queues = RabbitConfiguration.NOTIFICATION_QUEUE)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        if (!"SUCCEEDED".equals(event.status())) {
            log.info("No confirmation sent for payment {} with status {}", event.paymentId(), event.status());
            return;
        }
        emailService.sendPaymentConfirmation(event);
    }
}
