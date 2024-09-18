package dat.daos;

import java.util.List;
import java.util.Optional;

public interface IDAO<T> {
    void create(T entity);
    Optional<T> findById(Long id);
    List<T> findAll();
    void update(T entity);
    void delete(Long id);
    Optional<T> findByName(String name);
}
