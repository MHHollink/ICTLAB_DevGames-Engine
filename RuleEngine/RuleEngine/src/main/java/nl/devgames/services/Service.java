package nl.devgames.services;

/**
 * Created by Wouter on 1/9/2016.
 */
public interface Service<T> {
    Iterable<T> findAll();

    T find(Long id);

    void delete(Long id);

    T createOrUpdate(T object);
}
