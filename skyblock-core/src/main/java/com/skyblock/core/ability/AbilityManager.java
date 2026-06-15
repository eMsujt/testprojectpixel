package com.skyblock.core.ability;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AbilityManager} instead.
 */
@Deprecated
public final class AbilityManager {

    private AbilityManager() {}

    /** @deprecated Use {@link com.skyblock.core.manager.AbilityManager#getInstance()}. */
    @Deprecated
    public static com.skyblock.core.manager.AbilityManager getInstance() {
        return com.skyblock.core.manager.AbilityManager.getInstance();
    }
}
