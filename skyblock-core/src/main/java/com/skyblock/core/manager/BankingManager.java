package com.skyblock.core.manager;

import java.util.Objects;
import java.util.UUID;

/**
 * High-level coordinator for banking operations.
 *
 * <p>Moves coins atomically between the player's purse ({@link EconomyManager})
 * and their bank account ({@link BankManager}). Use this instead of calling
 * both managers separately to keep the two balances consistent.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BankingManager {

    private static final BankingManager INSTANCE = new BankingManager();

    private BankingManager() {}

    public static BankingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Moves {@code amount} coins from the player's purse into their bank account.
     *
     * @param playerId the player's UUID
     * @param amount   coins to deposit; must be positive and not exceed the purse balance
     * @return {@code true} if the deposit succeeded; {@code false} if the purse had insufficient funds
     */
    public boolean deposit(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive: " + amount);
        EconomyManager econ = EconomyManager.getInstance();
        if (!econ.withdraw(playerId, amount)) {
            return false;
        }
        BankManager.getInstance().deposit(playerId, amount);
        return true;
    }

    /**
     * Moves {@code amount} coins from the player's bank account into their purse.
     *
     * @param playerId the player's UUID
     * @param amount   coins to withdraw; must be positive and not exceed the bank balance
     * @return {@code true} if the withdrawal succeeded; {@code false} if the bank had insufficient funds
     */
    public boolean withdraw(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive: " + amount);
        BankManager bank = BankManager.getInstance();
        if (bank.getBalance(playerId) < amount) {
            return false;
        }
        bank.withdraw(playerId, amount);
        EconomyManager.getInstance().addPurse(playerId, amount);
        return true;
    }

    /** Returns the player's bank balance. */
    public double getBankBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return BankManager.getInstance().getBalance(playerId);
    }

    /** Returns the player's purse balance. */
    public long getPurseBalance(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return EconomyManager.getInstance().getPurse(playerId);
    }
}
