package rs.ac.festival.payment.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import rs.ac.festival.payment.domain.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByTicketId(String ticketId);
}
