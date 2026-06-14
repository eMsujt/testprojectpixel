package com.skyblock.plugin;

import com.skyblock.core.bank.BankManager;
import com.skyblock.core.collections.CollectionsManager;
import com.skyblock.dungeons.DungeonManager;
import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.menu.SkyblockMenuCommand;
import com.skyblock.slayers.SlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the SkyBlock plugin.
 *
 * <p>Holds the singleton plugin instance, owns the manager services for the
 * plugin's systems and drives the enable/disable lifecycle.</p>
 */
public final class SkyBlockPlugin extends JavaPlugin {

    private static SkyBlockPlugin instance;

    private CoinManager coinManager;
    private DungeonManager dungeonManager;
    private SlayerManager slayerManager;

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
        coinManager = new CoinManager();
        dungeonManager = new DungeonManager();
        slayerManager = new SlayerManager();
        BankManager.getInstance().load(getDataFolder());
        CollectionsManager.getInstance().load(getDataFolder());
        getCommand("skyblock").setExecutor(new SkyblockMenuCommand());
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
        BankManager.getInstance().save(getDataFolder());
        CollectionsManager.getInstance().save(getDataFolder());
        getLogger().info("SkyBlock plugin disabled.");
        instance = null;
    }

    /** Returns the coin purse service. */
    public CoinManager getCoinManager() {
        return coinManager;
    }

    /** Returns the dungeon session service. */
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    /** Returns the slayer quest service. */
    public SlayerManager getSlayerManager() {
        return slayerManager;
    }
}
