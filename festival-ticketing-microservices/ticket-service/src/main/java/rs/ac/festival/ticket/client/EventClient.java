package rs.ac.festival.ticket.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service", path = "/internal/festivals")
public interface EventClient {
    @GetMapping("/{id}")
    FestivalSummary getFestival(@PathVariable("id") Long id);
}
