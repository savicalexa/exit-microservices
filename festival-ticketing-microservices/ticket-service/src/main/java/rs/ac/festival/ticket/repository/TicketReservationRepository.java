package rs.ac.festival.ticket.repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.festival.ticket.domain.TicketReservation;
import rs.ac.festival.ticket.domain.TicketStatus;

public interface TicketReservationRepository extends JpaRepository<TicketReservation, UUID> {
    List<TicketReservation> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<TicketReservation> findTop100ByStatusAndExpiresAtBeforeOrderByExpiresAtAsc(TicketStatus status, Instant time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TicketReservation t where t.id = :id")
    Optional<TicketReservation> findByIdForUpdate(@Param("id") UUID id);
}
