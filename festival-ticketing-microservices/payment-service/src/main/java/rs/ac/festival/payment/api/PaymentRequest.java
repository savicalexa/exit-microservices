package rs.ac.festival.payment.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
    @NotNull UUID ticketId,
    @NotNull Long userId,
    @NotNull Long festivalId,
    @NotNull @Email String email,
    @NotNull @DecimalMin("0.01") BigDecimal amount
) {
}
