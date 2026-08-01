package rs.ac.festival.ticket.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.festival.ticket.domain.TicketInventory;

public interface TicketInventoryRepository extends JpaRepository<TicketInventory, Long> {
    @Modifying
    @Query(value = "INSERT INTO ticket_inventory (festival_id, active_tickets, version) " +
        "VALUES (:festivalId, 0, 0) ON CONFLICT (festival_id) DO NOTHING", nativeQuery = true)
    int initialize(@Param("festivalId") Long festivalId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from TicketInventory i where i.festivalId = :festivalId")
    Optional<TicketInventory> findForUpdate(@Param("festivalId") Long festivalId);
}
