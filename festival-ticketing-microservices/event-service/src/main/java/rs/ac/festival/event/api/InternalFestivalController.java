package rs.ac.festival.event.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.festival.event.service.FestivalService;

@RestController
@RequestMapping("/internal/festivals")
public class InternalFestivalController {
    private final FestivalService service;

    public InternalFestivalController(FestivalService service) { this.service = service; }

    @GetMapping("/{id}")
    public FestivalResponse get(@PathVariable Long id) { return service.get(id); }
}
