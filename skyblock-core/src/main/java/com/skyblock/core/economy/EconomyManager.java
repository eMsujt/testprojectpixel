package com.skyblock.core.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing player coin balances.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private static final double DEFAULT_BALANCE = 0.0;

    /** playerId -> coin balance */
    private final Map<UUID, Double> balances = new HashMap<>();

    private EconomyManager() {}

    /**
     * Returns the single shared {@code EconomyManager} instance.
     *
     * @return the singleton instance
     */
    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the coin balance for the given player, defaulting to 0 if not set.
     *
     * @param playerId UUID of the player
     * @return the player's current balance
     */
    public double getBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return balances.getOrDefault(playerId, DEFAULT_BALANCE);
    }

    /**
     * Sets the coin balance for the given player.
     *
     * @param playerId UUID of the player
     * @param amount   the new balance (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void setBalance(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("balance must not be negative");
        }
        balances.put(playerId, amount);
    }

    /**
     * Deposits coins into the given player's account.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to add (must be &gt; 0)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("deposit amount must be positive");
        }
        balances.merge(playerId, amount, Double::sum);
    }

    /**
     * Withdraws coins from the given player's account.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to subtract (must be &gt; 0)
     * @return {@code true} if the player had sufficient funds and the withdrawal succeeded
     * @throws IllegalArgumentException if amount is not positive
     */
    public boolean withdraw(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("withdrawal amount must be positive");
        }
        double current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        balances.put(playerId, current - amount);
        return true;
    }

    /**
     * Returns {@code true} if the player has at least the given amount.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to check
     */
    public boolean has(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        return getBalance(playerId) >= amount;
    }

    /** Removes all stored balances. */
    public void clear() {
        balances.clear();
    }
}
