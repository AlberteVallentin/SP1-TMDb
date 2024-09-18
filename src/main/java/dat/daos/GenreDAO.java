package dat.daos;

import dat.entities.Genre;

import java.util.List;
import java.util.Optional;

public class GenreDAO implements IDAO<Genre> {
    @Override
    public void create(Genre entity) {

    }

    @Override
    public Optional<Genre> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Genre> findAll() {
        return List.of();
    }

    @Override
    public void update(Genre entity) {

    }

    @Override
    public void delete(Long id) {

    }
}