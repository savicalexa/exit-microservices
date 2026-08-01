package rs.ac.festival.event.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "festivals")
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String naziv;

    @Column(nullable = false, length = 200)
    private String lokacija;

    @Column(name = "maksimalni_kapacitet", nullable = false)
    private Integer maksimalniKapacitet;

    @Version
    @Column(nullable = false)
    private Long version;

    protected Festival() {
    }

    public Festival(String naziv, String lokacija, Integer maksimalniKapacitet) {
        this.naziv = naziv;
        this.lokacija = lokacija;
        this.maksimalniKapacitet = maksimalniKapacitet;
    }

    public Long getId() { return id; }
    public String getNaziv() { return naziv; }
    public String getLokacija() { return lokacija; }
    public Integer getMaksimalniKapacitet() { return maksimalniKapacitet; }
    public Long getVersion() { return version; }

    public void update(String naziv, String lokacija, Integer maksimalniKapacitet) {
        this.naziv = naziv;
        this.lokacija = lokacija;
        this.maksimalniKapacitet = maksimalniKapacitet;
    }
}
