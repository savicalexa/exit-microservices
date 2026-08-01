package rs.ac.festival.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "ticket_inventory")
public class TicketInventory {
    @Id
    @Column(name = "festival_id")
    private Long festivalId;

    @Column(name = "active_tickets", nullable = false)
    private int activeTickets;

    @Version
    @Column(nullable = false)
    private Long version;

    protected TicketInventory() {
    }

    public Long getFestivalId() { return festivalId; }
    public int getActiveTickets() { return activeTickets; }

    public void allocate(int maximumCapacity) {
        if (activeTickets >= maximumCapacity) {
            throw new IllegalStateException("Festival capacity has been reached");
        }
        activeTickets++;
    }

    public void release() {
        if (activeTickets > 0) activeTickets--;
    }
}
