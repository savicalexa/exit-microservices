package rs.ac.festival.ticket.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ReservationRequest(
    @NotNull Long userId,
    @NotNull Long festivalId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal cena
) {
}
