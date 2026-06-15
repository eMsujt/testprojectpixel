package com.skyblock.economy;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.economy.EconomyManager} instead.
 */
@Deprecated
public final class CoinManager {

    private static final CoinManager INSTANCE = new CoinManager();

    public CoinManager() {}

    public static CoinManager getInstance() {
        return INSTANCE;
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#getBalance(UUID)}. */
    @Deprecated
    public synchronized long getBalance(UUID playerId) {
        return com.skyblock.core.economy.EconomyManager.getInstance().getPurse(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#has(UUID, double)}. */
    @Deprecated
    public synchronized boolean has(UUID playerId, long amount) {
        return com.skyblock.core.economy.EconomyManager.getInstance().has(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#deposit(UUID, double)}. */
    @Deprecated
    public synchronized long deposit(UUID playerId, long amount) {
        com.skyblock.core.economy.EconomyManager.getInstance().deposit(playerId, amount);
        return com.skyblock.core.economy.EconomyManager.getInstance().getPurse(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#withdraw(UUID, double)}. */
    @Deprecated
    public synchronized boolean withdraw(UUID playerId, long amount) {
        return com.skyblock.core.economy.EconomyManager.getInstance().withdraw(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#setBalance(UUID, double)}. */
    @Deprecated
    public synchronized void setBalance(UUID playerId, long balance) {
        com.skyblock.core.economy.EconomyManager.getInstance().setBalance(playerId, balance);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#clear(UUID)}. */
    @Deprecated
    public synchronized long clear(UUID playerId) {
        return com.skyblock.core.economy.EconomyManager.getInstance().clear(playerId);
    }
}
