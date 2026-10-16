package ma.hamza.heritage.model.tableperclass;

import javax.persistence.*;

@Entity
@Table(name = "electroniques")
public class Electronique extends Produit {
    @Column(nullable = false)
    private int garantie;

    public Electronique() {}

    public Electronique(String nom, int garantie) {
        super(nom);
        this.garantie = garantie;
    }

    public int getGarantie() { return garantie; }
    public void setGarantie(int garantie) { this.garantie = garantie; }

    @Override
    public String toString() {
        return "Electronique{id=" + getId() + ", nom='" + getNom()
                + "', garantie=" + garantie + "}";
    }
}
