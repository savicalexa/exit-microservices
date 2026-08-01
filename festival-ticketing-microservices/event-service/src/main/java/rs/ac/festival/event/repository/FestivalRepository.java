package rs.ac.festival.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.festival.event.domain.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {
    boolean existsByNazivIgnoreCaseAndLokacijaIgnoreCase(String naziv, String lokacija);
}
