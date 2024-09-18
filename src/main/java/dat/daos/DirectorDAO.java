package dat.daos;

import dat.entities.Director;

import java.util.List;
import java.util.Optional;

public class DirectorDAO implements IDAO<Director> {

    @Override
    public void create(Director entity) {

    }

    @Override
    public Optional<Director> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Director> findAll() {
        return List.of();
    }

    @Override
    public void update(Director entity) {

    }

    @Override
    public void delete(Long id) {

    }
}
