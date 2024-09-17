package dat.daos;

import java.util.List;

public interface IDAO <T, ID> {
    T getById(ID id);
    void insert(T t);
    void update(T t);
    void delete(T t);
    void deleteById(ID id);
    List<T> getAll();


}
