package it.epicode.library.repository;

import it.epicode.library.model.user.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository extends AbstractRepository<User> {

    // Email index for unique email constraint
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>(); // email -> userId

    @Override
    protected void updateSecondaryIndexes(User entity, User previous) {
        // Remove from old indexes if updating
        if (previous != null) {
            removeFromSecondaryIndexes(previous);
        }

        // Add to email index
        emailIndex.put(entity.getEmail().toLowerCase(), entity.getId());
    }

    @Override
    protected void removeFromSecondaryIndexes(User entity) {
        emailIndex.remove(entity.getEmail().toLowerCase());
    }

    /**
     * Finds user by email.
     */
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();

        String userId = emailIndex.get(email.toLowerCase());
        return userId != null ? findById(userId) : Optional.empty();
    }

    /**
     * Finds active users.
     */
    public List<User> findActiveUsers() {
        return findAll(User::isActive);
    }

    /**
     * Checks if email is already taken.
     */
    public boolean emailExists(String email) {
        return email != null && emailIndex.containsKey(email.toLowerCase());
    }
}