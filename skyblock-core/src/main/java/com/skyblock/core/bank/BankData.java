package com.skyblock.core.bank;

import java.util.Objects;
import java.util.UUID;

/**
 * Holds the persistent bank state for a single player.
 */
public final class BankData {

    private final UUID playerId;
    private double balance;

    public BankData(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId, "playerId");
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("insufficient balance: has " + this.balance + ", requested " + amount);
        }
        this.balance -= amount;
    }
}
