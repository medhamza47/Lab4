package ma.hamza.heritage.model.joined;

import javax.persistence.*;

@Entity
@Table(name = "developpeurs")
public class Developpeur extends Employe {
    @Column(nullable = false)
    private String langage;

    public Developpeur() {}

    public Developpeur(String nom, String langage) {
        super(nom);
        this.langage = langage;
    }

    public String getLangage() { return langage; }
    public void setLangage(String langage) { this.langage = langage; }

    @Override
    public String toString() {
        return "Developpeur{id=" + getId() + ", nom='" + getNom()
                + "', langage=" + langage + "}";
    }
}
