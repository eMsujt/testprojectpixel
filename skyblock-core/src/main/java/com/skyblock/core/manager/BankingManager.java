package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BankingManager {

    private static final BankingManager INSTANCE = new BankingManager();

    /** Maximum coin balance a player may hold in the bank. */
    public static final long MAX_BALANCE = 2_000_000_000L;

    private final Map<UUID, Long> balances = new HashMap<>();

    private BankingManager() {}

    public static BankingManager getInstance() {
        return INSTANCE;
    }

    public long getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0L);
    }

    public void deposit(UUID playerId, long amount) {
        if (amount <= 0) {
            return;
        }
        long room = MAX_BALANCE - getBalance(playerId);
        balances.put(playerId, getBalance(playerId) + Math.min(room, amount));
    }

    public boolean withdraw(UUID playerId, long amount) {
        long current = getBalance(playerId);
        if (amount <= 0 || current < amount) {
            return false;
        }
        balances.put(playerId, current - amount);
        return true;
    }
}
