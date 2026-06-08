package database;

import java.util.List;
import exception.DatabaseException;

public interface DAO<T> {
    List<T> getAll() throws DatabaseException;
    T getById(int id) throws DatabaseException;
    void save(T t) throws DatabaseException;
    void update(T t) throws DatabaseException;
    void delete(int id) throws DatabaseException;
}
