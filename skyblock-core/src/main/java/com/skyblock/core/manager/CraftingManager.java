package com.skyblock.core.manager;

/**
 * @deprecated Use {@link com.skyblock.core.crafting.manager.CraftingManager} directly.
 */
@Deprecated
public final class CraftingManager {

    private CraftingManager() {}

    public static com.skyblock.core.crafting.manager.CraftingManager getInstance() {
        return com.skyblock.core.crafting.manager.CraftingManager.getInstance();
    }
}
