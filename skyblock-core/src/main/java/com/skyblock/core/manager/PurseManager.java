package com.skyblock.core.manager;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton facade for player coin-purse operations.
 * State is delegated to {@link EconomyManager} to avoid duplicate maps.
 */
public final class PurseManager {

    private static final PurseManager INSTANCE = new PurseManager();

    private final EconomyManager economy = EconomyManager.getInstance();

    private PurseManager() {}

    public static PurseManager getInstance() {
        return INSTANCE;
    }

    /** Returns the coin balance for the given player, defaulting to 0. */
    public double getCoins(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return economy.getCoins(playerId);
    }

    /** Adds {@code amount} coins to the player's purse (must be >= 0). */
    public void addCoins(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        economy.addCoins(playerId, amount);
    }

    /**
     * Removes {@code amount} coins from the player's purse.
     *
     * @return {@code true} if the player had sufficient funds and the coins were deducted
     */
    public boolean removeCoins(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        return economy.removeCoins(playerId, amount);
    }
}
