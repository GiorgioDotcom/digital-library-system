package it.epicode.library.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

public abstract class AbstractRepository<T extends Identifiable> implements Repository<T> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    // Thread-safe storage using ConcurrentHashMap
    protected final Map<String, T> storage = new ConcurrentHashMap<>();

    // ReadWriteLock for complex operations requiring consistency
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    // Secondary indexes for performance (example: by type)
    protected final Map<String, Set<String>> secondaryIndexes = new ConcurrentHashMap<>();

    @Override
    public Optional<T> findById(String id) {
        if (id == null) return Optional.empty();

        lock.readLock().lock();
        try {
            return Optional.ofNullable(storage.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(storage.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<T> findAll(Predicate<T> filter) {
        if (filter == null) return findAll();

        lock.readLock().lock();
        try {
            return storage.values().stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public T save(T entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity and ID cannot be null");
        }

        lock.writeLock().lock();
        try {
            T previous = storage.put(entity.getId(), entity);
            updateSecondaryIndexes(entity, previous);

            if (previous == null) {
                logger.log(Level.INFO, "Created new entity: {0}", entity.getId());
            } else {
                logger.log(Level.INFO, "Updated entity: {0}", entity.getId());
            }

            return entity;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<T> saveAll(Collection<T> entities) {
        if (entities == null) return Collections.emptyList();

        List<T> savedEntities = new ArrayList<>();
        lock.writeLock().lock();
        try {
            for (T entity : entities) {
                if (entity != null && entity.getId() != null) {
                    T previous = storage.put(entity.getId(), entity);
                    updateSecondaryIndexes(entity, previous);
                    savedEntities.add(entity);
                }
            }

            logger.log(Level.INFO, "Batch saved {0} entities", savedEntities.size());
            return savedEntities;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) return false;

        lock.writeLock().lock();
        try {
            T removed = storage.remove(id);
            if (removed != null) {
                removeFromSecondaryIndexes(removed);
                logger.log(Level.INFO, "Deleted entity: {0}", id);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean delete(T entity) {
        return entity != null && deleteById(entity.getId());
    }

    @Override
    public void deleteAll() {
        lock.writeLock().lock();
        try {
            int count = storage.size();
            storage.clear();
            secondaryIndexes.clear();
            logger.log(Level.INFO, "Deleted all {0} entities", count);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public boolean existsById(String id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public Optional<T> findFirst(Predicate<T> filter) {
        if (filter == null) return Optional.empty();

        lock.readLock().lock();
        try {
            return storage.values().stream()
                    .filter(filter)
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<T> findByIds(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        lock.readLock().lock();
        try {
            return ids.stream()
                    .map(storage::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Map<String, T> findAllAsMap() {
        lock.readLock().lock();
        try {
            return new HashMap<>(storage);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getAllIds() {
        return new HashSet<>(storage.keySet());
    }

    /**
     * Updates secondary indexes when entity is saved.
     * Subclasses can override to maintain custom indexes.
     */
    protected abstract void updateSecondaryIndexes(T entity, T previous);

    /**
     * Removes entity from secondary indexes when deleted.
     * Subclasses can override to maintain custom indexes.
     */
    protected abstract void removeFromSecondaryIndexes(T entity);

    /**
     * Gets repository statistics.
     */
    public RepositoryStats getStats() {
        lock.readLock().lock();
        try {
            return new RepositoryStats(
                    storage.size(),
                    secondaryIndexes.size(),
                    getClass().getSimpleName()
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Repository statistics inner class.
     */
    public static class RepositoryStats {
        private final int entityCount;
        private final int indexCount;
        private final String repositoryType;
        private final long timestamp;

        public RepositoryStats(int entityCount, int indexCount, String repositoryType) {
            this.entityCount = entityCount;
            this.indexCount = indexCount;
            this.repositoryType = repositoryType;
            this.timestamp = System.currentTimeMillis();
        }

        public int getEntityCount() { return entityCount; }
        public int getIndexCount() { return indexCount; }
        public String getRepositoryType() { return repositoryType; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("%s{entities=%d, indexes=%d}",
                    repositoryType, entityCount, indexCount);
        }
    }
}