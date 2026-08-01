package rs.ac.festival.ticket.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.festival.ticket.api.ReservationRequest;
import rs.ac.festival.ticket.api.TicketPurchaseResponse;
import rs.ac.festival.ticket.api.TicketResponse;
import rs.ac.festival.ticket.client.EventClient;
import rs.ac.festival.ticket.client.PaymentClient;
import rs.ac.festival.ticket.client.PaymentRequest;
import rs.ac.festival.ticket.client.UserClient;
import rs.ac.festival.ticket.domain.TicketInventory;
import rs.ac.festival.ticket.domain.TicketReservation;
import rs.ac.festival.ticket.domain.TicketStatus;
import rs.ac.festival.ticket.repository.TicketInventoryRepository;
import rs.ac.festival.ticket.repository.TicketReservationRepository;
import rs.ac.festival.ticket.support.ConflictException;
import rs.ac.festival.ticket.support.ResourceNotFoundException;

@Service
public class TicketReservationService {
    private final TicketReservationRepository ticketRepository;
    private final TicketInventoryRepository inventoryRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final PaymentClient paymentClient;
    private final Duration reservationTtl;

    public TicketReservationService(
        TicketReservationRepository ticketRepository,
        TicketInventoryRepository inventoryRepository,
        UserClient userClient,
        EventClient eventClient,
        PaymentClient paymentClient,
        @Value("${ticket.reservation-ttl:PT15M}") Duration reservationTtl
    ) {
        this.ticketRepository = ticketRepository;
        this.inventoryRepository = inventoryRepository;
        this.userClient = userClient;
        this.eventClient = eventClient;
        this.paymentClient = paymentClient;
        this.reservationTtl = reservationTtl;
    }

    @Transactional
    public TicketResponse reserve(ReservationRequest request) {
        var user = userClient.getUser(request.userId());
        if (!user.active()) throw new ConflictException("User account is inactive");
        var festival = eventClient.getFestival(request.festivalId());

        inventoryRepository.initialize(festival.id());
        TicketInventory inventory = inventoryRepository.findForUpdate(festival.id())
            .orElseThrow(() -> new IllegalStateException("Inventory initialization failed"));
        if (inventory.getActiveTickets() >= festival.maksimalniKapacitet()) {
            throw new ConflictException("Festival is sold out");
        }
        inventory.allocate(festival.maksimalniKapacitet());

        Instant now = Instant.now();
        TicketReservation ticket = new TicketReservation(
            user.id(), festival.id(), user.email(), festival.naziv(), request.cena(), now, now.plus(reservationTtl)
        );
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse get(UUID id) { return TicketResponse.from(find(id)); }

    @Transactional(readOnly = true)
    public List<TicketResponse> forUser(Long userId) {
        return ticketRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(TicketResponse::from).toList();
    }

    @Transactional
    public TicketPurchaseResponse purchase(UUID id) {
        TicketReservation ticket = lock(id);
        if (ticket.getStatus() == TicketStatus.PURCHASED) {
            throw new ConflictException("Ticket has already been purchased");
        }
        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new ConflictException("Only an active reservation can be purchased");
        }
        if (ticket.hasExpired(Instant.now())) {
            expire(ticket);
            throw new ConflictException("Reservation has expired");
        }

        ticket.markPaymentPending();
        var payment = paymentClient.process(new PaymentRequest(
            ticket.getId(), ticket.getUserId(), ticket.getFestivalId(), ticket.getUserEmail(), ticket.getAmount()
        ));
        return new TicketPurchaseResponse(TicketResponse.from(ticket), payment);
    }

    @Transactional
    public TicketResponse cancel(UUID id) {
        TicketReservation ticket = lock(id);
        if (ticket.getStatus() != TicketStatus.RESERVED) {
            throw new ConflictException("Only an active reservation can be cancelled");
        }
        TicketInventory inventory = inventoryRepository.findForUpdate(ticket.getFestivalId())
            .orElseThrow(() -> new IllegalStateException("Ticket inventory was not found"));
        inventory.release();
        ticket.cancel();
        return TicketResponse.from(ticket);
    }

    @Transactional
    public void markPurchased(UUID ticketId, Instant paidAt) {
        TicketReservation ticket = lock(ticketId);
        if (ticket.getStatus() == TicketStatus.PURCHASED) return;
        if (ticket.getStatus() != TicketStatus.PAYMENT_PENDING && ticket.getStatus() != TicketStatus.RESERVED) {
            throw new ConflictException("Payment event cannot be applied to ticket in state " + ticket.getStatus());
        }
        ticket.markPurchased(paidAt);
    }

    @Scheduled(fixedDelayString = "${ticket.reservation-cleanup-ms:60000}")
    @Transactional
    public void expireReservations() {
        var expired = ticketRepository.findTop100ByStatusAndExpiresAtBeforeOrderByExpiresAtAsc(
            TicketStatus.RESERVED, Instant.now()
        );
        expired.forEach(this::expire);
    }

    private void expire(TicketReservation ticket) {
        TicketInventory inventory = inventoryRepository.findForUpdate(ticket.getFestivalId())
            .orElseThrow(() -> new IllegalStateException("Ticket inventory was not found"));
        inventory.release();
        ticket.expire();
    }

    private TicketReservation find(UUID id) {
        return ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket " + id + " was not found"));
    }

    private TicketReservation lock(UUID id) {
        return ticketRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket " + id + " was not found"));
    }
}
