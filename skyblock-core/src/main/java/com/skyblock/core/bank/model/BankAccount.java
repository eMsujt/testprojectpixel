package com.skyblock.core.bank.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable snapshot of a player's bank account.
 *
 * @param balance            current coin balance (non-negative)
 * @param transactionHistory human-readable log of past transactions, oldest first
 */
public record BankAccount(double balance, List<String> transactionHistory) {

    /** Creates a fresh account with zero balance and no history. */
    public BankAccount(double balance) {
        this(balance, new ArrayList<>());
    }

    public BankAccount {
        Objects.requireNonNull(transactionHistory, "transactionHistory");
        if (balance < 0) {
            throw new IllegalArgumentException("balance must not be negative: " + balance);
        }
    }
}
