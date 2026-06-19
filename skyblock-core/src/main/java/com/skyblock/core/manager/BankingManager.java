package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankingManager {

    private static final BankingManager INSTANCE = new BankingManager();

    private final Map<UUID, Double> balances = new HashMap<>();

    private BankingManager() {}

    public static BankingManager getInstance() {
        return INSTANCE;
    }

    public double getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0.0);
    }

    public void deposit(UUID playerId, double amount) {
        balances.merge(playerId, amount, Double::sum);
    }

    public boolean withdraw(UUID playerId, double amount) {
        double current = getBalance(playerId);
        if (current < amount) {
            return false;
        }
        balances.put(playerId, current - amount);
        return true;
    }
}
