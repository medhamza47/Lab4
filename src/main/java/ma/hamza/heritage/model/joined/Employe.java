package ma.hamza.heritage.model.joined;

import javax.persistence.*;

@Entity
@Table(name = "employes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    public Employe() {}

    public Employe(String nom) {
        this.nom = nom;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
