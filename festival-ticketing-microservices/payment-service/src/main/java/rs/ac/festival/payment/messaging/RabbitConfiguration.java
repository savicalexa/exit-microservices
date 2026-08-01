package rs.ac.festival.payment.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    public static final String EXCHANGE = "festival.events";
    public static final String PAYMENT_COMPLETED_KEY = "payment.completed";
    public static final String TICKET_QUEUE = "ticket.payment.completed";
    public static final String NOTIFICATION_QUEUE = "notification.payment.completed";

    @Bean TopicExchange festivalExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue ticketPaymentQueue() { return new Queue(TICKET_QUEUE, true); }
    @Bean Queue notificationPaymentQueue() { return new Queue(NOTIFICATION_QUEUE, true); }

    @Bean Binding ticketPaymentBinding(Queue ticketPaymentQueue, TopicExchange festivalExchange) {
        return BindingBuilder.bind(ticketPaymentQueue).to(festivalExchange).with(PAYMENT_COMPLETED_KEY);
    }

    @Bean Binding notificationPaymentBinding(Queue notificationPaymentQueue, TopicExchange festivalExchange) {
        return BindingBuilder.bind(notificationPaymentQueue).to(festivalExchange).with(PAYMENT_COMPLETED_KEY);
    }

    @Bean MessageConverter messageConverter() { return new JacksonJsonMessageConverter(); }
}
