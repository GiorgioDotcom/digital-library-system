package it.epicode.library.repository;

import java.util.*;
import java.util.function.Predicate;

/**
 * Generic repository interface defining basic CRUD operations.
 * Uses advanced generics with bounded type parameters.
 */
public interface Repository<T extends Identifiable> {

    Optional<T> findById(String id);
    List<T> findAll();
    List<T> findAll(Predicate<T> filter);
    T save(T entity);
    List<T> saveAll(Collection<T> entities);
    boolean deleteById(String id);
    boolean delete(T entity);
    void deleteAll();
    long count();
    boolean existsById(String id);

    // Advanced query methods
    Optional<T> findFirst(Predicate<T> filter);
    List<T> findByIds(Collection<String> ids);
    Map<String, T> findAllAsMap();
    Set<String> getAllIds();
}