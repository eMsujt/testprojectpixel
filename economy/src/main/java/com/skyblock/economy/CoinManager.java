package com.skyblock.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player coin purses.
 *
 * <p>Balances are stored in a {@link HashMap} keyed by player UUID. All
 * access goes through {@code synchronized} methods so deposits and
 * withdrawals are safe to call from any thread.</p>
 */
public final class CoinManager {

    private final Map<UUID, Long> purses = new HashMap<>();

    /**
     * Returns the player's current purse balance, or {@code 0} if the
     * player has no purse yet.
     *
     * @param playerId the player's UUID
     * @return the current balance in coins
     */
    public synchronized long getBalance(UUID playerId) {
        return purses.getOrDefault(playerId, 0L);
    }

    /**
     * Returns whether the player can afford the given amount.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to check, must be non-negative
     * @return {@code true} if the player's balance is at least {@code amount}
     */
    public synchronized boolean has(UUID playerId, long amount) {
        requireNonNegative(amount);
        return getBalance(playerId) >= amount;
    }

    /**
     * Adds coins to the player's purse.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to deposit, must be non-negative
     * @return the new balance after the deposit
     * @throws ArithmeticException if the deposit would overflow the purse
     */
    public synchronized long deposit(UUID playerId, long amount) {
        requireNonNegative(amount);
        long updated = Math.addExact(getBalance(playerId), amount);
        purses.put(playerId, updated);
        return updated;
    }

    /**
     * Removes coins from the player's purse if the balance covers it.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to withdraw, must be non-negative
     * @return {@code true} if the withdrawal succeeded, {@code false} if
     *         the player's balance was insufficient
     */
    public synchronized boolean withdraw(UUID playerId, long amount) {
        requireNonNegative(amount);
        long current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        purses.put(playerId, current - amount);
        return true;
    }

    /**
     * Sets the player's purse balance directly.
     *
     * @param playerId the player's UUID
     * @param balance  the new balance, must be non-negative
     */
    public synchronized void setBalance(UUID playerId, long balance) {
        requireNonNegative(balance);
        purses.put(playerId, balance);
    }

    /**
     * Removes the player's purse entirely (e.g. on data wipe).
     *
     * @param playerId the player's UUID
     * @return the balance the purse held, or {@code 0} if there was none
     */
    public synchronized long clear(UUID playerId) {
        Long removed = purses.remove(playerId);
        return removed != null ? removed : 0L;
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
