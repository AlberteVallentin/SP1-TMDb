package dat.daos;

import dat.entities.Actor;
import dat.entities.Director;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<Director> {

    EntityManagerFactory emf;

    public  DirectorDAO(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public void create(Director entity) {

        try(EntityManager em= emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }

    }

    @Override
    public Optional<Director> findById(Long id) {
        try(EntityManager em= emf.createEntityManager()){
            Director director = em.find(Director.class,id);

            return director != null? Optional.of(director) : Optional.empty();

        }
    }

    @Override
    public List<Director> findAll() {
        try(EntityManager em= emf.createEntityManager()){
            return em.createQuery("SELECT d FROM Director d", Director.class).getResultList();
        }
    }

    @Override
    public void update(Director entity) {
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

            Director director = em.find(Director.class,id);
            if(director != null){
                em.remove(director);
            }
            em.getTransaction().commit();
        }

    }

    @Override
    public Optional<Director> findByName(String name) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            TypedQuery<Director> query=em.createQuery("SELECT d FROM Director d WHERE d.name=:name",Director.class);
            query.setParameter("name",name);

           List<Director> director = query.getResultList();
            // Check if the list is empty, return an Optional accordingly
            return director.isEmpty() ? Optional.empty() : Optional.of(director.get(0));
        }
    }
}
