package com.skyblock.core.bank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankManager {

    private final Map<UUID, Double> balances = new HashMap<>();

    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        balances.merge(playerId, amount, Double::sum);
    }

    public boolean withdraw(UUID playerId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        double current = getBalance(playerId);
        if (current < amount) return false;
        balances.put(playerId, current - amount);
        return true;
    }
}
