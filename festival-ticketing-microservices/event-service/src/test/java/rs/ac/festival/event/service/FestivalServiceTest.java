package rs.ac.festival.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.festival.event.api.FestivalRequest;
import rs.ac.festival.event.domain.Festival;
import rs.ac.festival.event.repository.FestivalRepository;
import rs.ac.festival.event.support.ConflictException;

@ExtendWith(MockitoExtension.class)
class FestivalServiceTest {
    @Mock FestivalRepository repository;
    @InjectMocks FestivalService service;

    @Test
    void createsFestivalWhenItIsUnique() {
        FestivalRequest request = new FestivalRequest("Exit", "Novi Sad", 50000);
        when(repository.existsByNazivIgnoreCaseAndLokacijaIgnoreCase("Exit", "Novi Sad")).thenReturn(false);
        when(repository.save(any(Festival.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertThat(response.naziv()).isEqualTo("Exit");
        assertThat(response.maksimalniKapacitet()).isEqualTo(50000);
        verify(repository).save(any(Festival.class));
    }

    @Test
    void rejectsDuplicateFestival() {
        FestivalRequest request = new FestivalRequest("Exit", "Novi Sad", 50000);
        when(repository.existsByNazivIgnoreCaseAndLokacijaIgnoreCase("Exit", "Novi Sad")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(ConflictException.class);
    }

    @Test
    void updatesExistingFestival() {
        Festival festival = new Festival("Old", "Belgrade", 100);
        when(repository.findById(1L)).thenReturn(Optional.of(festival));

        var response = service.update(1L, new FestivalRequest("New", "Novi Sad", 200));

        assertThat(response.naziv()).isEqualTo("New");
        assertThat(response.maksimalniKapacitet()).isEqualTo(200);
    }
}
