package com.skyblock.bank;

import java.util.Objects;
import java.util.UUID;

/**
 * Data holder for a single player bank account.
 *
 * <p>Each account is identified by its own {@code accountId} and tracks the
 * owning player, the current balance, and the time the account was opened.
 * The identity fields ({@code accountId}, {@code playerId}, {@code createdAt})
 * are immutable; the balance may be updated via {@link #setBalance(double)}.</p>
 */
public final class BankAccount {

    private final UUID accountId;
    private final UUID playerId;
    private double balance;
    private final long createdAt;

    /**
     * Creates a new bank account.
     *
     * @param accountId the unique identifier of the account
     * @param playerId  the UUID of the owning player
     * @param balance   the opening balance, must be non-negative
     * @param createdAt the time the account was opened, in epoch milliseconds
     */
    public BankAccount(UUID accountId, UUID playerId, double balance, long createdAt) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.playerId = Objects.requireNonNull(playerId, "playerId");
        requireNonNegative(balance);
        this.balance = balance;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier of this account.
     *
     * @return the account id
     */
    public UUID getAccountId() {
        return accountId;
    }

    /**
     * Returns the UUID of the player who owns this account.
     *
     * @return the owning player's UUID
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Returns the current balance of this account.
     *
     * @return the current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the balance of this account.
     *
     * @param balance the new balance, must be non-negative
     */
    public void setBalance(double balance) {
        requireNonNegative(balance);
        this.balance = balance;
    }

    /**
     * Returns the time this account was created.
     *
     * @return the creation time in epoch milliseconds
     */
    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "BankAccount{accountId=" + accountId + ", playerId=" + playerId
                + ", balance=" + balance + ", createdAt=" + createdAt + '}';
    }

    private static void requireNonNegative(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("balance must be non-negative: " + value);
        }
    }
}
