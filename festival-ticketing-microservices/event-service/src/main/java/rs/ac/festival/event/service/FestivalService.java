package rs.ac.festival.event.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.festival.event.api.FestivalRequest;
import rs.ac.festival.event.api.FestivalResponse;
import rs.ac.festival.event.domain.Festival;
import rs.ac.festival.event.repository.FestivalRepository;
import rs.ac.festival.event.support.ConflictException;
import rs.ac.festival.event.support.ResourceNotFoundException;

@Service
public class FestivalService {
    private final FestivalRepository repository;

    public FestivalService(FestivalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FestivalResponse create(FestivalRequest request) {
        if (repository.existsByNazivIgnoreCaseAndLokacijaIgnoreCase(request.naziv(), request.lokacija())) {
            throw new ConflictException("Festival with the same name and location already exists");
        }
        Festival festival = new Festival(request.naziv(), request.lokacija(), request.maksimalniKapacitet());
        return FestivalResponse.from(repository.save(festival));
    }

    @Transactional(readOnly = true)
    public FestivalResponse get(Long id) {
        return FestivalResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<FestivalResponse> list() {
        return repository.findAll().stream().map(FestivalResponse::from).toList();
    }

    @Transactional
    public FestivalResponse update(Long id, FestivalRequest request) {
        Festival festival = find(id);
        festival.update(request.naziv(), request.lokacija(), request.maksimalniKapacitet());
        return FestivalResponse.from(festival);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Festival find(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Festival " + id + " was not found"));
    }
}
