package com.skyblock.economy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player bank accounts and maintains a full transaction history.
 *
 * <p>Bank accounts are separate from the coin purse ({@link CoinManager}):
 * coins must be explicitly deposited into or withdrawn from the bank.
 * All access is {@code synchronized} for thread safety.</p>
 */
public final class BankManager {

    /**
     * An immutable record of a single bank transaction.
     *
     * @param txId     unique transaction identifier
     * @param playerId the player this transaction belongs to
     * @param amount   the coin amount (always non-negative)
     * @param type     transaction type, e.g. {@code "DEPOSIT"}, {@code "WITHDRAW"}, {@code "INTEREST"}
     */
    public record BankTransaction(UUID txId, UUID playerId, long amount, String type) {}

    private final Map<UUID, Long> accounts = new HashMap<>();
    private final Map<UUID, List<BankTransaction>> history = new HashMap<>();

    /**
     * Returns the player's current bank balance, or {@code 0} if no account exists.
     *
     * @param playerId the player's UUID
     * @return the current balance in coins
     */
    public synchronized long getBalance(UUID playerId) {
        return accounts.getOrDefault(playerId, 0L);
    }

    /**
     * Deposits coins into the player's bank account and records the transaction.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to deposit, must be non-negative
     * @param type     the transaction type label (e.g. {@code "DEPOSIT"})
     * @return the new balance after the deposit
     * @throws ArithmeticException if the deposit would overflow the account
     */
    public synchronized long deposit(UUID playerId, long amount, String type) {
        requireNonNegative(amount);
        long updated = Math.addExact(getBalance(playerId), amount);
        accounts.put(playerId, updated);
        record(new BankTransaction(UUID.randomUUID(), playerId, amount, type));
        return updated;
    }

    /**
     * Withdraws coins from the player's bank account and records the transaction.
     *
     * @param playerId the player's UUID
     * @param amount   the amount to withdraw, must be non-negative
     * @param type     the transaction type label (e.g. {@code "WITHDRAW"})
     * @return {@code true} if the withdrawal succeeded, {@code false} if the
     *         balance was insufficient
     */
    public synchronized boolean withdraw(UUID playerId, long amount, String type) {
        requireNonNegative(amount);
        long current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        accounts.put(playerId, current - amount);
        record(new BankTransaction(UUID.randomUUID(), playerId, amount, type));
        return true;
    }

    /**
     * Returns an unmodifiable view of the player's transaction history,
     * oldest first.
     *
     * @param playerId the player's UUID
     * @return list of transactions, never {@code null}
     */
    public synchronized List<BankTransaction> getHistory(UUID playerId) {
        return Collections.unmodifiableList(
                history.getOrDefault(playerId, Collections.emptyList()));
    }

    /**
     * Removes the player's account and transaction history entirely.
     *
     * @param playerId the player's UUID
     * @return the balance the account held, or {@code 0} if there was none
     */
    public synchronized long clear(UUID playerId) {
        history.remove(playerId);
        Long removed = accounts.remove(playerId);
        return removed != null ? removed : 0L;
    }

    private void record(BankTransaction tx) {
        history.computeIfAbsent(tx.playerId(), k -> new ArrayList<>()).add(tx);
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
