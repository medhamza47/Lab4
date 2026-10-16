package ma.hamza.heritage.model.singletable;

import javax.persistence.*;

@Entity
@Table(name = "vehicules", indexes = @Index(name = "idx_vehicule_type", columnList = "vehicule_type"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicule_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marque;

    public Vehicule() {}

    public Vehicule(String marque) {
        this.marque = marque;
    }

    public Long getId() { return id; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
}
