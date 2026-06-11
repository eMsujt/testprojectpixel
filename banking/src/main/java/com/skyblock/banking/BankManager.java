package com.skyblock.banking;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player bank account balances.
 *
 * <p>Balances are stored in a {@link ConcurrentHashMap} keyed by player
 * UUID. Updates use atomic {@code compute} operations so deposits and
 * withdrawals are safe to call from any thread.</p>
 */
public final class BankManager {

    private final ConcurrentHashMap<UUID, Long> balances = new ConcurrentHashMap<>();

    /**
     * Returns the player's current bank balance, or {@code 0} if the
     * player has no account yet.
     *
     * @param playerId the player's UUID
     * @return the current balance in coins
     */
    public long getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0L);
    }

    /**
     * Returns whether the player's bank balance covers the given amount.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to check, must be non-negative
     * @return {@code true} if the player's balance is at least {@code amount}
     */
    public boolean has(UUID playerId, long amount) {
        requireNonNegative(amount);
        return getBalance(playerId) >= amount;
    }

    /**
     * Adds coins to the player's bank account.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to deposit, must be non-negative
     * @return the new balance after the deposit
     * @throws ArithmeticException if the deposit would overflow the account
     */
    public long deposit(UUID playerId, long amount) {
        requireNonNegative(amount);
        return balances.compute(playerId,
                (id, balance) -> Math.addExact(balance != null ? balance : 0L, amount));
    }

    /**
     * Removes coins from the player's bank account if the balance covers it.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to withdraw, must be non-negative
     * @return {@code true} if the withdrawal succeeded, {@code false} if
     *         the player's balance was insufficient
     */
    public boolean withdraw(UUID playerId, long amount) {
        requireNonNegative(amount);
        boolean[] success = {false};
        balances.compute(playerId, (id, balance) -> {
            long current = balance != null ? balance : 0L;
            if (current < amount) {
                return balance;
            }
            success[0] = true;
            return current - amount;
        });
        return success[0];
    }

    /**
     * Sets the player's bank balance directly.
     *
     * @param playerId the player's UUID
     * @param balance  the new balance, must be non-negative
     */
    public void setBalance(UUID playerId, long balance) {
        requireNonNegative(balance);
        balances.put(playerId, balance);
    }

    /**
     * Removes the player's bank account entirely (e.g. on data wipe).
     *
     * @param playerId the player's UUID
     * @return the balance the account held, or {@code 0} if there was none
     */
    public long clear(UUID playerId) {
        Long removed = balances.remove(playerId);
        return removed != null ? removed : 0L;
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
