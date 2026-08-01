package rs.ac.festival.payment.api;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = service.process(request);
        return ResponseEntity.created(URI.create("/api/payments/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable String id) { return service.get(id); }
}
