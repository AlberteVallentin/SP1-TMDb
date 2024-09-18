package dat.daos;

import dat.entities.Actor;

import java.util.List;
import java.util.Optional;

public class ActorDAO implements IDAO<Actor> {

    @Override
    public void create(Actor entity) {

    }

    @Override
    public Optional<Actor> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Actor> findAll() {
        return List.of();
    }

    @Override
    public void update(Actor entity) {

    }

    @Override
    public void delete(Long id) {

    }
}
