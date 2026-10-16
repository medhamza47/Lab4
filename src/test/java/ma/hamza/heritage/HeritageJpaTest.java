package ma.hamza.heritage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.EntityManager;
import ma.hamza.heritage.model.singletable.*;
import ma.hamza.heritage.model.joined.*;
import ma.hamza.heritage.model.tableperclass.*;
import ma.hamza.heritage.service.PersistanceService;
import org.junit.*;
import static org.junit.Assert.*;

public class HeritageJpaTest {
    private PersistanceService service;

    @Before
    public void preparer() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("javax.persistence.jdbc.url", "jdbc:h2:mem:test_" + UUID.randomUUID());
        configuration.put("hibernate.show_sql", "false");
        service = new PersistanceService(configuration);
    }

    @After
    public void fermer() {
        if (service != null) { service.close(); }
    }

    private long nombre(EntityManager em, String sql) {
        return ((Number) em.createNativeQuery(sql).getSingleResult()).longValue();
    }

    @Test
    public void crudSingleTable() {
        Voiture premier = new Voiture("Dacia", 5);
        Moto second = new Moto("Yamaha", false);
        service.creer(premier, second);
        assertNotNull(premier.getId());
        assertNotNull(second.getId());
        assertNotEquals(premier.getId(), second.getId());
        List<Vehicule> resultats = service.lister(Vehicule.class);
        assertEquals(2, resultats.size());
        assertTrue(service.trouver(Vehicule.class, premier.getId()) instanceof Voiture);
        assertTrue(service.trouver(Vehicule.class, second.getId()) instanceof Moto);
        assertEquals(1, service.lister(Voiture.class).size());
        premier.setNombrePortes(3);
        service.modifier(premier);
        assertEquals(3, service.trouver(Voiture.class, premier.getId()).getNombrePortes());
        service.supprimer(Vehicule.class, second.getId());
        assertNull(service.trouver(Vehicule.class, second.getId()));
        assertNull(service.trouver(Moto.class, second.getId()));
        assertEquals(1, service.lister(Vehicule.class).size());
        service.supprimer(Vehicule.class, premier.getId());
        assertTrue(service.lister(Vehicule.class).isEmpty());
    }

    @Test
    public void crudJoined() {
        Developpeur premier = new Developpeur("Hamza", "Java");
        Manager second = new Manager("Salma", 2);
        service.creer(premier, second);
        assertNotNull(premier.getId());
        assertNotNull(second.getId());
        assertNotEquals(premier.getId(), second.getId());
        List<Employe> resultats = service.lister(Employe.class);
        assertEquals(2, resultats.size());
        assertTrue(service.trouver(Employe.class, premier.getId()) instanceof Developpeur);
        assertTrue(service.trouver(Employe.class, second.getId()) instanceof Manager);
        assertEquals(1, service.lister(Developpeur.class).size());
        premier.setLangage("Kotlin");
        service.modifier(premier);
        assertEquals("Kotlin", service.trouver(Developpeur.class, premier.getId()).getLangage());
        service.supprimer(Employe.class, second.getId());
        assertNull(service.trouver(Employe.class, second.getId()));
        assertNull(service.trouver(Manager.class, second.getId()));
        assertEquals(1, service.lister(Employe.class).size());
        service.supprimer(Employe.class, premier.getId());
        assertTrue(service.lister(Employe.class).isEmpty());
    }

    @Test
    public void crudTablePerClass() {
        Livre premier = new Livre("Antigone", "Jean Anouilh");
        Electronique second = new Electronique("Ordinateur", 24);
        service.creer(premier, second);
        assertNotNull(premier.getId());
        assertNotNull(second.getId());
        assertNotEquals(premier.getId(), second.getId());
        List<Produit> resultats = service.lister(Produit.class);
        assertEquals(2, resultats.size());
        assertTrue(service.trouver(Produit.class, premier.getId()) instanceof Livre);
        assertTrue(service.trouver(Produit.class, second.getId()) instanceof Electronique);
        assertEquals(1, service.lister(Livre.class).size());
        premier.setAuteur("Auteur modifié");
        service.modifier(premier);
        assertEquals("Auteur modifié", service.trouver(Livre.class, premier.getId()).getAuteur());
        service.supprimer(Produit.class, second.getId());
        assertNull(service.trouver(Produit.class, second.getId()));
        assertNull(service.trouver(Electronique.class, second.getId()));
        assertEquals(1, service.lister(Produit.class).size());
        service.supprimer(Produit.class, premier.getId());
        assertTrue(service.lister(Produit.class).isEmpty());
    }

    @Test
    public void verifierTableUniqueEtDiscriminant() {
        service.creer(new Voiture("Dacia", 5), new Moto("Yamaha", false));
        service.transaction(em -> {
            assertEquals(2, nombre(em, "SELECT COUNT(*) FROM vehicules"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM vehicules WHERE vehicule_type='VOITURE' AND nombrePortes=5 AND sidecar IS NULL"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM vehicules WHERE vehicule_type='MOTO' AND sidecar=FALSE AND nombrePortes IS NULL"));
            assertEquals(0, nombre(em, "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME IN ('VOITURE', 'MOTO')"));
            return null;
        });
    }

    @Test
    public void verifierJointuresEtNettoyageTableParente() {
        Developpeur developpeur = new Developpeur("Hamza", "Java");
        Manager manager = new Manager("Salma", 2);
        service.creer(developpeur, manager);
        service.transaction(em -> {
            assertEquals(2, nombre(em, "SELECT COUNT(*) FROM employes"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM employes e JOIN developpeurs d ON d.id=e.id WHERE d.langage='Java'"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM employes e JOIN managers m ON m.id=e.id WHERE m.nombreEquipes=2"));
            return null;
        });
        service.supprimer(Employe.class, manager.getId());
        service.transaction(em -> {
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM employes"));
            assertEquals(0, nombre(em, "SELECT COUNT(*) FROM managers"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM developpeurs"));
            return null;
        });
    }

    @Test
    public void verifierTablesConcretesEtIdentifiantsCommuns() {
        Livre livre = new Livre("Antigone", "Jean Anouilh");
        Electronique appareil = new Electronique("Ordinateur", 24);
        service.creer(livre, appareil);
        assertNotEquals(livre.getId(), appareil.getId());
        service.transaction(em -> {
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM livres WHERE nom='Antigone' AND auteur='Jean Anouilh'"));
            assertEquals(1, nombre(em, "SELECT COUNT(*) FROM electroniques WHERE nom='Ordinateur' AND garantie=24"));
            assertEquals(0, nombre(em, "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME='PRODUIT'"));
            return null;
        });
        assertEquals(2, service.lister(Produit.class).size());
    }

    @Test
    public void annulerTouteLaTransactionEtReutiliserLaFabrique() {
        assertThrows(RuntimeException.class, () -> service.creer(
                new Voiture("Dacia", 5), new Developpeur(null, "Java")));
        assertTrue(service.lister(Vehicule.class).isEmpty());
        assertTrue(service.lister(Employe.class).isEmpty());
        service.creer(new Manager("Salma", 2));
        assertEquals(1, service.lister(Employe.class).size());
    }
}
