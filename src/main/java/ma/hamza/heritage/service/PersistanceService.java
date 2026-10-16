package ma.hamza.heritage.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.*;

public class PersistanceService implements AutoCloseable {
    private final EntityManagerFactory fabrique;

    public PersistanceService() { this(Collections.emptyMap()); }

    public PersistanceService(Map<String, ?> configuration) {
        fabrique = Persistence.createEntityManagerFactory("heritagePU", configuration);
    }

    public <T> T transaction(Function<EntityManager, T> operation) {
        EntityManager em = fabrique.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T resultat = operation.apply(em);
            tx.commit();
            return resultat;
        } catch (RuntimeException erreur) {
            try {
                if (tx.isActive()) { tx.rollback(); }
            } catch (RuntimeException annulation) {
                erreur.addSuppressed(annulation);
            }
            throw erreur;
        } finally {
            em.close();
        }
    }

    public void creer(Object... entites) {
        transaction(em -> {
            for (Object entite : entites) { em.persist(entite); }
            return null;
        });
    }

    public <T> T trouver(Class<T> type, Long id) {
        return transaction(em -> em.find(type, id));
    }

    public <T> List<T> lister(Class<T> type) {
        return transaction(em -> {
            String nom = em.getMetamodel().entity(type).getName();
            return em.createQuery("SELECT e FROM " + nom + " e ORDER BY e.id", type).getResultList();
        });
    }

    public <T> T modifier(T entite) {
        return transaction(em -> em.merge(entite));
    }

    public <T> void supprimer(Class<T> type, Long id) {
        transaction(em -> {
            T entite = em.find(type, id);
            if (entite == null) { throw new IllegalArgumentException("Entité introuvable : " + id); }
            em.remove(entite);
            return null;
        });
    }

    @Override
    public void close() {
        if (fabrique.isOpen()) { fabrique.close(); }
    }
}
