package rs.ac.festival.event.api;

import rs.ac.festival.event.domain.Festival;

public record FestivalResponse(Long id, String naziv, String lokacija, Integer maksimalniKapacitet) {
    public static FestivalResponse from(Festival festival) {
        return new FestivalResponse(
            festival.getId(), festival.getNaziv(), festival.getLokacija(), festival.getMaksimalniKapacitet()
        );
    }
}
