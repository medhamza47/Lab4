package ma.hamza.heritage.model.joined;

import javax.persistence.*;

@Entity
@Table(name = "managers")
public class Manager extends Employe {
    @Column(nullable = false)
    private int nombreEquipes;

    public Manager() {}

    public Manager(String nom, int nombreEquipes) {
        super(nom);
        this.nombreEquipes = nombreEquipes;
    }

    public int getNombreEquipes() { return nombreEquipes; }
    public void setNombreEquipes(int nombreEquipes) { this.nombreEquipes = nombreEquipes; }

    @Override
    public String toString() {
        return "Manager{id=" + getId() + ", nom='" + getNom()
                + "', nombreEquipes=" + nombreEquipes + "}";
    }
}
