package rs.ac.festival.event.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FestivalRequest(
    @NotBlank @Size(max = 200) String naziv,
    @NotBlank @Size(max = 200) String lokacija,
    @NotNull @Positive Integer maksimalniKapacitet
) {
}
