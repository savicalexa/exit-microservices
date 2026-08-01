package rs.ac.festival.ticket.client;

import java.util.Set;

public record UserSummary(Long id, String email, String ime, Set<String> roles, boolean active) {
}
