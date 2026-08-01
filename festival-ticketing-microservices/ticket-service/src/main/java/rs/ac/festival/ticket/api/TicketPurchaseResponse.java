package rs.ac.festival.ticket.api;

import rs.ac.festival.ticket.client.PaymentResponse;

public record TicketPurchaseResponse(TicketResponse ticket, PaymentResponse payment) {
}
