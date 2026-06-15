package com.skyblock.core.banking;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.BankManager} instead.
 */
@Deprecated
public final class BankingManager {

    private static final BankingManager INSTANCE = new BankingManager();

    /** profileId -> coin balance */
    private final Map<UUID, Double> balances = new HashMap<>();

    private BankingManager() {}

    public static BankingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the coin balance for the given profile, defaulting to {@code 0} if not set.
     *
     * @param profileId the profile's unique id
     * @return the current balance
     */
    public double getBalance(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId");
        return balances.getOrDefault(profileId, 0.0);
    }

    /**
     * Deposits {@code amount} coins into the profile's balance.
     *
     * @param profileId the profile to deposit into
     * @param amount    the amount to deposit (must be positive)
     * @return the new balance after the deposit
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public double deposit(UUID profileId, double amount) {
        Objects.requireNonNull(profileId, "profileId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double newBalance = getBalance(profileId) + amount;
        balances.put(profileId, newBalance);
        return newBalance;
    }

    /**
     * Withdraws {@code amount} coins from the profile's balance.
     *
     * @param profileId the profile to withdraw from
     * @param amount    the amount to withdraw (must be positive and not exceed the balance)
     * @return the new balance after the withdrawal
     * @throws IllegalArgumentException if {@code amount} is not positive or exceeds the balance
     */
    public double withdraw(UUID profileId, double amount) {
        Objects.requireNonNull(profileId, "profileId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double current = getBalance(profileId);
        if (amount > current) {
            throw new IllegalArgumentException("insufficient balance: has " + current + ", requested " + amount);
        }
        double newBalance = current - amount;
        balances.put(profileId, newBalance);
        return newBalance;
    }

    /** Removes all stored balance data. */
    public void clear() {
        balances.clear();
    }
}
