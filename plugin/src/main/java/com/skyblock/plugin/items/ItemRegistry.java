package com.skyblock.plugin.items;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Ensures the bundled {@code items.yml} definitions are present in the plugin
 * data folder.
 *
 * <p>On {@link #load(JavaPlugin)} the registry copies the {@code items.yml}
 * resource out of the jar on first run, leaving any existing user-edited copy
 * untouched.</p>
 */
public final class ItemRegistry {

    private static final ItemRegistry INSTANCE = new ItemRegistry();

    private ItemRegistry() {
    }

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Copies the bundled {@code items.yml} into the plugin data folder if it is
     * not already present.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists() && plugin.getResource("items.yml") != null) {
            plugin.saveResource("items.yml", false);
            plugin.getLogger().info("Copied default items.yml to the data folder.");
        }
    }
}
