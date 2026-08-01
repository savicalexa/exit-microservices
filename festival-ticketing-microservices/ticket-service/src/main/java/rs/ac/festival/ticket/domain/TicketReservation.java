package rs.ac.festival.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class TicketReservation {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "festival_id", nullable = false)
    private Long festivalId;

    @Column(name = "user_email", nullable = false, length = 254)
    private String userEmail;

    @Column(name = "festival_name", nullable = false, length = 200)
    private String festivalName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected TicketReservation() {
    }

    public TicketReservation(Long userId, Long festivalId, String userEmail, String festivalName,
                             BigDecimal amount, Instant createdAt, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.festivalId = festivalId;
        this.userEmail = userEmail;
        this.festivalName = festivalName;
        this.amount = amount;
        this.status = TicketStatus.RESERVED;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getFestivalId() { return festivalId; }
    public String getUserEmail() { return userEmail; }
    public String getFestivalName() { return festivalName; }
    public BigDecimal getAmount() { return amount; }
    public TicketStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getPaidAt() { return paidAt; }

    public boolean hasExpired(Instant now) { return expiresAt.isBefore(now); }
    public void markPaymentPending() { this.status = TicketStatus.PAYMENT_PENDING; }
    public void markPurchased(Instant paidAt) { this.status = TicketStatus.PURCHASED; this.paidAt = paidAt; }
    public void cancel() { this.status = TicketStatus.CANCELLED; }
    public void expire() { this.status = TicketStatus.EXPIRED; }
}
