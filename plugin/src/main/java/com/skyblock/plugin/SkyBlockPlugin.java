package com.skyblock.plugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Paper plugin entry point for the SkyBlock remake.
 *
 * <p>Drives the enable/disable lifecycle of the plugin.</p>
 */
public final class SkyBlockPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyBlock plugin disabled.");
    }
}
