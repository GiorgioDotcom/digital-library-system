package it.epicode.library.model.user;

import it.epicode.library.repository.Identifiable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Loan implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String userId;
    private final String mediaId;
    private final LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private int renewalCount;
    private final int maxRenewals;

    public Loan(String userId, String mediaId, int loanDays) {
        this.id = UUID.randomUUID().toString();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.mediaId = Objects.requireNonNull(mediaId, "Media ID cannot be null");
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(loanDays);
        this.isReturned = false;
        this.renewalCount = 0;
        this.maxRenewals = 2; // Default max renewals
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Marks the loan as returned.
     */
    public void returnMedia() {
        this.returnDate = LocalDate.now();
        this.isReturned = true;
    }

    /**
     * Renews the loan if possible.
     */
    public boolean renewLoan(int additionalDays) {
        if (renewalCount >= maxRenewals) {
            return false;
        }

        this.dueDate = dueDate.plusDays(additionalDays);
        this.renewalCount++;
        return true;
    }

    /**
     * Checks if the loan is overdue.
     */
    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Gets days until due (negative if overdue).
     */
    public long getDaysUntilDue() {
        return LocalDate.now().until(dueDate).getDays();
    }

    // Getters
    public String getUserId() { return userId; }
    public String getMediaId() { return mediaId; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }
    public int getRenewalCount() { return renewalCount; }
    public int getMaxRenewals() { return maxRenewals; }
    public boolean canRenew() { return renewalCount < maxRenewals; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan loan = (Loan) obj;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Loan{id='%s', userId='%s', mediaId='%s', due=%s, returned=%s}",
                id, userId, mediaId, dueDate, isReturned);
    }
}