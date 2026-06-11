package com.skyblock.core;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the SkyBlock core plugin.
 *
 * <p>Holds the singleton plugin instance and drives the enable/disable
 * lifecycle for all core systems.</p>
 */
public final class SkyBlockPlugin extends JavaPlugin {

    private static SkyBlockPlugin instance;

    /**
     * Returns the active plugin instance.
     *
     * @return the singleton {@link SkyBlockPlugin} instance
     * @throws IllegalStateException if the plugin has not been enabled yet
     */
    public static SkyBlockPlugin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SkyBlockPlugin is not enabled");
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("SkyBlock core enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyBlock core disabled.");
        instance = null;
    }
}
