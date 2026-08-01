package rs.ac.festival.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field("ticket_id")
    private String ticketId;

    @Field("user_id")
    private Long userId;

    @Field("festival_id")
    private Long festivalId;

    private String email;
    private BigDecimal amount;
    private PaymentStatus status;

    @Field("created_at")
    private Instant createdAt;

    protected Payment() {
    }

    public Payment(UUID ticketId, Long userId, Long festivalId, String email, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();
        this.ticketId = ticketId.toString();
        this.userId = userId;
        this.festivalId = festivalId;
        this.email = email;
        this.amount = amount;
        this.status = PaymentStatus.SUCCEEDED;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getTicketId() { return ticketId; }
    public Long getUserId() { return userId; }
    public Long getFestivalId() { return festivalId; }
    public String getEmail() { return email; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
