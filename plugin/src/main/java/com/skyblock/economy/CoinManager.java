package com.skyblock.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's spendable coin balances: their {@code purse} (coins on
 * hand) and their {@code bank} (stored coins).
 *
 * <p>A shared instance is available via {@link #getInstance()} for callers that
 * want a single source of truth, while a public constructor is retained for
 * components that own their own instance.</p>
 */
public final class CoinManager {

    private static final CoinManager INSTANCE = new CoinManager();

    private final Map<UUID, Long> purse = new HashMap<>();
    private final Map<UUID, Long> bank = new HashMap<>();

    public CoinManager() {}

    public static CoinManager getInstance() {
        return INSTANCE;
    }

    public long getPurse(UUID playerId) {
        return purse.getOrDefault(playerId, 0L);
    }

    public void setPurse(UUID playerId, long amount) {
        purse.put(playerId, Math.max(0L, amount));
    }

    public void addPurse(UUID playerId, long amount) {
        setPurse(playerId, getPurse(playerId) + amount);
    }

    public long getBank(UUID playerId) {
        return bank.getOrDefault(playerId, 0L);
    }

    public void setBank(UUID playerId, long amount) {
        bank.put(playerId, Math.max(0L, amount));
    }

    public void addBank(UUID playerId, long amount) {
        setBank(playerId, getBank(playerId) + amount);
    }

    /** Purse balance, exposed under the name menus/shops expect. */
    public long getBalance(UUID playerId) {
        return getPurse(playerId);
    }

    /**
     * Removes {@code amount} coins from the player's purse if they can afford it.
     *
     * @return {@code true} if the coins were withdrawn, {@code false} otherwise
     */
    public boolean withdraw(UUID playerId, long amount) {
        if (amount <= 0 || getPurse(playerId) < amount) {
            return false;
        }
        setPurse(playerId, getPurse(playerId) - amount);
        return true;
    }
}
