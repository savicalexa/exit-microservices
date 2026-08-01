package rs.ac.festival.ticket.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.ticket.service.TicketReservationService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketReservationService service;
    public TicketController(TicketReservationService service) { this.service = service; }

    @PostMapping("/reservations")
    public ResponseEntity<TicketResponse> reserve(@Valid @RequestBody ReservationRequest request) {
        TicketResponse created = service.reserve(request);
        return ResponseEntity.created(URI.create("/api/tickets/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable UUID id) { return service.get(id); }

    @GetMapping
    public List<TicketResponse> forUser(@RequestParam Long userId) { return service.forUser(userId); }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<TicketPurchaseResponse> purchase(@PathVariable UUID id) {
        return ResponseEntity.accepted().body(service.purchase(id));
    }

    @PostMapping("/{id}/cancel")
    public TicketResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
}
