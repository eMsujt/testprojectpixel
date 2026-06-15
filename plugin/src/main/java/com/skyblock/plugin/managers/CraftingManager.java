package com.skyblock.plugin.managers;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @deprecated Use {@link com.skyblock.core.crafting.CraftingManager} instead.
 */
@Deprecated
public final class CraftingManager {

    private static final CraftingManager INSTANCE = new CraftingManager();

    private CraftingManager() {}

    public static CraftingManager getInstance() {
        return INSTANCE;
    }

    public void registerRecipes(JavaPlugin plugin) {
        com.skyblock.core.crafting.CraftingManager.getInstance().registerRecipes(plugin);
    }
}
