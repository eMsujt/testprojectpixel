package com.skyblock.core.economy.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing player coin balances (purse and bank).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private static final double DEFAULT_BALANCE = 0.0;

    /** playerId -> purse balance */
    private final Map<UUID, Double> balances = new HashMap<>();
    /** playerId -> bank balance */
    private final Map<UUID, Double> bankBalances = new HashMap<>();

    private EconomyManager() {}

    /**
     * Returns the single shared {@code EconomyManager} instance.
     *
     * @return the singleton instance
     */
    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Purse (primary balance)
    // -------------------------------------------------------------------------

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

    /** Alias for {@link #getBalance(UUID)} returning a {@code long}. */
    public long getPurse(UUID playerId) {
        return (long) getBalance(playerId);
    }

    /** Alias for {@link #getBalance(UUID)}. */
    public double getCoins(UUID playerId) {
        return getBalance(playerId);
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

    /** Alias for {@link #setBalance(UUID, double)} accepting a {@code long}. */
    public void setPurse(UUID playerId, long amount) {
        setBalance(playerId, Math.max(0L, amount));
    }

    /**
     * Deposits coins into the given player's account.
     *
     * @param playerId UUID of the player
     * @param amount   the amount to add (must be &gt;= 0)
     * @throws IllegalArgumentException if amount is negative
     */
    public void deposit(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("deposit amount must not be negative");
        }
        balances.merge(playerId, amount, Double::sum);
    }

    /** Alias for {@link #deposit(UUID, double)} accepting a {@code long}. */
    public void addPurse(UUID playerId, long amount) {
        deposit(playerId, amount);
    }

    /** Alias for {@link #deposit(UUID, double)}. */
    public void addCoins(UUID playerId, double amount) {
        deposit(playerId, amount);
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

    /** Alias for {@link #withdraw(UUID, double)} accepting a {@code long}. */
    public boolean withdraw(UUID playerId, long amount) {
        return withdraw(playerId, (double) amount);
    }

    /** Alias for {@link #withdraw(UUID, double)}. */
    public boolean removeCoins(UUID playerId, double amount) {
        return withdraw(playerId, amount);
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

    /** Alias for {@link #has(UUID, double)}. */
    public boolean hasCoins(UUID playerId, double amount) {
        return has(playerId, amount);
    }

    // -------------------------------------------------------------------------
    // Bank (stored balance)
    // -------------------------------------------------------------------------

    /** Returns the player's bank balance, defaulting to 0. */
    public long getBank(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bankBalances.getOrDefault(playerId, DEFAULT_BALANCE).longValue();
    }

    /** Sets the player's bank balance (must be &gt;= 0). */
    public void setBank(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        bankBalances.put(playerId, (double) Math.max(0L, amount));
    }

    /** Adds {@code amount} to the player's bank balance. */
    public void addBank(UUID playerId, long amount) {
        setBank(playerId, getBank(playerId) + amount);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /** Removes all stored purse and bank balances. */
    public void clear() {
        balances.clear();
        bankBalances.clear();
    }

    /** Removes all stored balances for a single player. */
    public long clear(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        bankBalances.remove(playerId);
        Double removed = balances.remove(playerId);
        return removed != null ? removed.longValue() : 0L;
    }
}
