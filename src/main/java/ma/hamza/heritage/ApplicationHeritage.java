package ma.hamza.heritage;

import java.util.Arrays;
import ma.hamza.heritage.model.singletable.*;
import ma.hamza.heritage.model.joined.*;
import ma.hamza.heritage.model.tableperclass.*;
import ma.hamza.heritage.service.PersistanceService;
import org.h2.tools.Server;

public class ApplicationHeritage {
    public static void main(String[] args) throws Exception {
        try (PersistanceService service = new PersistanceService()) {
            Voiture voiture = new Voiture("Dacia", 5);
            Moto moto = new Moto("Yamaha", false);
            Developpeur developpeur = new Developpeur("Hamza", "Java");
            Manager manager = new Manager("Salma", 2);
            Livre livre = new Livre("La Boîte à merveilles", "Ahmed Sefrioui");
            Electronique ordinateur = new Electronique("Ordinateur portable", 24);

            System.out.println("\n=== Création des six entités ===");
            service.creer(voiture, moto, developpeur, manager, livre, ordinateur);
            afficher(service);

            System.out.println("\n=== Modification dans les trois hiérarchies ===");
            voiture.setNombrePortes(3);
            developpeur.setLangage("Kotlin");
            livre.setNom("Antigone");
            livre.setAuteur("Jean Anouilh");
            service.modifier(voiture);
            service.modifier(developpeur);
            service.modifier(livre);
            System.out.println(service.trouver(Vehicule.class, voiture.getId()));
            System.out.println(service.trouver(Employe.class, developpeur.getId()));
            System.out.println(service.trouver(Produit.class, livre.getId()));

            System.out.println("\n=== Suppression via les classes parentes ===");
            service.supprimer(Vehicule.class, moto.getId());
            service.supprimer(Employe.class, manager.getId());
            service.supprimer(Produit.class, ordinateur.getId());
            afficher(service);

            if (Arrays.asList(args).contains("--console")) {
                Server console = Server.createWebServer("-webPort", "8082").start();
                try {
                    System.out.println("\nConsole H2 : http://localhost:8082");
                    System.out.println("JDBC : jdbc:h2:mem:heritage | Utilisateur : sa | Mot de passe vide");
                    System.out.println("Appuyez sur Entrée pour arrêter et supprimer le schéma.");
                    System.in.read();
                } finally { console.stop(); }
            }
        }
    }

    private static void afficher(PersistanceService service) {
        System.out.println("\n--- SINGLE_TABLE : une table et une colonne discriminante ---");
        service.lister(Vehicule.class).forEach(System.out::println);
        System.out.println("\n--- JOINED : tables liées par des jointures ---");
        service.lister(Employe.class).forEach(System.out::println);
        System.out.println("\n--- TABLE_PER_CLASS : tables concrètes et UNION ALL ---");
        service.lister(Produit.class).forEach(System.out::println);
    }
}
