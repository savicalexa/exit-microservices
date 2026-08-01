package rs.ac.festival.event.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.event.service.FestivalService;

@RestController
@RequestMapping("/api/festivals")
public class FestivalController {
    private final FestivalService service;

    public FestivalController(FestivalService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<FestivalResponse> create(@Valid @RequestBody FestivalRequest request) {
        FestivalResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/festivals/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public FestivalResponse get(@PathVariable Long id) { return service.get(id); }

    @GetMapping
    public List<FestivalResponse> list() { return service.list(); }

    @PutMapping("/{id}")
    public FestivalResponse update(@PathVariable Long id, @Valid @RequestBody FestivalRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
