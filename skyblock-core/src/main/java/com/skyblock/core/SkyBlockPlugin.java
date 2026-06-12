package com.skyblock.core;

import com.skyblock.core.combat.CombatManager;
import com.skyblock.core.commands.SkyBlockCommand;
import com.skyblock.core.listeners.SkyBlockEventListener;
import com.skyblock.core.menu.MenuManager;
import com.skyblock.farming.FarmingManager;
import com.skyblock.foraging.ForagingManager;
import com.skyblock.mining.MiningManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the SkyBlock core plugin.
 *
 * <p>Initialises the singleton managers, registers the {@code /skyblock}
 * command, and registers all event listeners.</p>
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

        PlayerDataManager playerDataManager = PlayerDataManager.getInstance();
        MiningManager miningManager = MiningManager.getInstance();
        FarmingManager farmingManager = new FarmingManager();
        ForagingManager foragingManager = new ForagingManager();

        getCommand("skyblock").setExecutor(new SkyBlockCommand(playerDataManager));
        getServer().getPluginManager().registerEvents(
                new SkyBlockEventListener(miningManager, farmingManager, foragingManager), this);
        getServer().getPluginManager().registerEvents(CombatManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(MenuManager.getInstance(), this);

        getLogger().info("SkyBlock core enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkyBlock core disabled.");
        instance = null;
    }
}
