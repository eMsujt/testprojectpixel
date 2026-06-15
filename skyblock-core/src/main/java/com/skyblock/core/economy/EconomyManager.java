package com.skyblock.core.economy;

/**
 * @deprecated Use {@link com.skyblock.core.economy.manager.EconomyManager} instead.
 */
@Deprecated
public final class EconomyManager {
    private EconomyManager() {}

    /** @deprecated Use {@link com.skyblock.core.economy.manager.EconomyManager#getInstance()} instead. */
    @Deprecated
    public static com.skyblock.core.economy.manager.EconomyManager getInstance() {
        return com.skyblock.core.economy.manager.EconomyManager.getInstance();
    }
}
