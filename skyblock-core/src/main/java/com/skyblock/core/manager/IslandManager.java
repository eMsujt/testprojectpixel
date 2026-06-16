package com.skyblock.core.manager;

/**
 * @deprecated Use {@link com.skyblock.core.island.manager.IslandManager} instead.
 */
@Deprecated
public final class IslandManager {

    private IslandManager() {
    }

    /** @deprecated Use {@link com.skyblock.core.island.manager.IslandManager#getInstance()} */
    @Deprecated
    public static com.skyblock.core.island.manager.IslandManager getInstance() {
        return com.skyblock.core.island.manager.IslandManager.getInstance();
    }
}
