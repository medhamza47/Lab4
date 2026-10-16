package ma.hamza.heritage.model.tableperclass;

import javax.persistence.*;

@Entity
@Table(name = "livres")
public class Livre extends Produit {
    @Column(nullable = false)
    private String auteur;

    public Livre() {}

    public Livre(String nom, String auteur) {
        super(nom);
        this.auteur = auteur;
    }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    @Override
    public String toString() {
        return "Livre{id=" + getId() + ", nom='" + getNom()
                + "', auteur=" + auteur + "}";
    }
}
