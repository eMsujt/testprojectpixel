package com.skyblock.plugin.manager;

import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.economy.EconomyManager} instead.
 */
@Deprecated
public final class EconomyManager {

    private static final EconomyManager INSTANCE = new EconomyManager();

    private EconomyManager() {
    }

    public static EconomyManager getInstance() {
        return INSTANCE;
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#getCoins(UUID)}. */
    @Deprecated
    public double getCoins(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return com.skyblock.core.economy.EconomyManager.getInstance().getCoins(uuid);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#addCoins(UUID, double)}. */
    @Deprecated
    public void addCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        com.skyblock.core.economy.EconomyManager.getInstance().addCoins(uuid, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#removeCoins(UUID, double)}. */
    @Deprecated
    public boolean removeCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        return com.skyblock.core.economy.EconomyManager.getInstance().removeCoins(uuid, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.economy.EconomyManager#hasCoins(UUID, double)}. */
    @Deprecated
    public boolean hasCoins(UUID uuid, double amount) {
        Objects.requireNonNull(uuid, "uuid");
        return com.skyblock.core.economy.EconomyManager.getInstance().hasCoins(uuid, amount);
    }
}
