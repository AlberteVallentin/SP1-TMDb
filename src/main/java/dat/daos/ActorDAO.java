package dat.daos;
import dat.entities.Actor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Builder;

import java.util.List;
import java.util.Optional;
@Builder

public class ActorDAO implements IDAO<Actor> {

    private EntityManagerFactory emf;

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Actor entity) {
        try (EntityManager em = emf.createEntityManager()) {
            // Check if the actor already exists
            Optional<Actor> existingActor = findByName(entity.getName());
            if (existingActor.isPresent()) {
                System.out.println("Actor with the name '" + entity.getName() + "' already exists.");
                return;  // Avoid inserting a duplicate actor
            }

            em.getTransaction().begin();
            em.persist(entity);  // Persist the new actor
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Optional<Actor> findById(Long id) {

        try(EntityManager em= emf.createEntityManager()){
            Actor actor= em.find(Actor.class, id);
            return actor!=null? Optional.of(actor): Optional.empty();
        }

    }

    @Override
    public List<Actor> findAll() {
        try(EntityManager em= emf.createEntityManager()){
            return em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();
        }

    }

    @Override
    public void update(Actor entity) {

            try(EntityManager em= emf.createEntityManager()){
                em.getTransaction().begin();
                em.merge(entity);
                em.getTransaction().commit();
            }

    }

    @Override
    public void delete(Long id) {

            try(EntityManager em= emf.createEntityManager()){
                em.getTransaction().begin();
                Actor actor= em.find(Actor.class, id);
                if(actor!=null){
                    em.remove(actor);
                }
                em.getTransaction().commit();
            }
    }

    @Override
    public Optional<Actor> findByName(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            // Query to find the actor by name
            List<Actor> actors = em.createQuery("SELECT a FROM Actor a WHERE a.name = :name", Actor.class)
                    .setParameter("name", name)
                    .getResultList();

            return actors.isEmpty() ? Optional.empty() : Optional.of(actors.get(0));
        }
    }
}
