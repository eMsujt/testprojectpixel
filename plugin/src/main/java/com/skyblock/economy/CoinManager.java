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

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#getPurse(UUID)}. */
    @Deprecated
    public long getPurse(UUID playerId) {
        return com.skyblock.core.economy.EconomyManager.getInstance().getPurse(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#setPurse(UUID, long)}. */
    @Deprecated
    public void setPurse(UUID playerId, long amount) {
        com.skyblock.core.economy.EconomyManager.getInstance().setPurse(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#addPurse(UUID, long)}. */
    @Deprecated
    public void addPurse(UUID playerId, long amount) {
        com.skyblock.core.economy.EconomyManager.getInstance().addPurse(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#getBank(UUID)}. */
    @Deprecated
    public long getBank(UUID playerId) {
        return com.skyblock.core.economy.EconomyManager.getInstance().getBank(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#setBank(UUID, long)}. */
    @Deprecated
    public void setBank(UUID playerId, long amount) {
        com.skyblock.core.economy.EconomyManager.getInstance().setBank(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#addBank(UUID, long)}. */
    @Deprecated
    public void addBank(UUID playerId, long amount) {
        com.skyblock.core.economy.EconomyManager.getInstance().addBank(playerId, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#getBalance(UUID)}. */
    @Deprecated
    public long getBalance(UUID playerId) {
        return com.skyblock.core.economy.EconomyManager.getInstance().getPurse(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#withdraw(UUID, double)}. */
    @Deprecated
    public boolean withdraw(UUID playerId, long amount) {
        return com.skyblock.core.economy.EconomyManager.getInstance().withdraw(playerId, amount);
    }
}
