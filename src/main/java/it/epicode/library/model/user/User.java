package it.epicode.library.model.user;

import it.epicode.library.repository.Identifiable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate registrationDate;
    private boolean isActive;
    private int maxLoans;

    public User(String firstName, String lastName, String email) {
        this.id = UUID.randomUUID().toString();
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.registrationDate = LocalDate.now();
        this.isActive = true;
        this.maxLoans = 5; // Default max loans
    }

    @Override
    public String getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public int getMaxLoans() { return maxLoans; }
    public void setMaxLoans(int maxLoans) {
        if (maxLoans < 0) throw new IllegalArgumentException("Max loans cannot be negative");
        this.maxLoans = maxLoans;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', active=%s}",
                id, getFullName(), email, isActive);
    }
}