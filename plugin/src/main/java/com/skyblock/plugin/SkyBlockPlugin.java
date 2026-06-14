package com.skyblock.plugin;

import com.skyblock.core.collections.CollectionsManager;
import com.skyblock.dungeons.DungeonManager;
import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.command.auctionhouse.AuctionHouseCommand;
import com.skyblock.plugin.command.collections.CollectionsCommand;
import com.skyblock.plugin.command.fairy.FairyCommand;
import com.skyblock.plugin.command.hotm.HotmCommand;
import com.skyblock.plugin.command.island.IslandCommand;
import com.skyblock.plugin.command.kuudra.KuudraCommand;
import com.skyblock.plugin.command.mayor.MayorCommand;
import com.skyblock.plugin.command.pets.PetsCommand;
import com.skyblock.plugin.command.skills.SkillsCommand;
import com.skyblock.plugin.command.slayer.SlayerCommand;
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
        CollectionsManager.getInstance().load(getDataFolder());
        getCommand("skyblock").setExecutor(new SkyblockMenuCommand());
        AuctionHouseCommand auctionHouseCommand = new AuctionHouseCommand();
        getCommand("auctionhouse").setExecutor(auctionHouseCommand);
        if (getCommand("ah") != null) {
            getCommand("ah").setExecutor(auctionHouseCommand);
        }
        getCommand("collections").setExecutor(new CollectionsCommand());
        getCommand("fairy").setExecutor(new FairyCommand());
        getCommand("hotm").setExecutor(new HotmCommand());
        getCommand("island").setExecutor(new IslandCommand());
        getCommand("kuudra").setExecutor(new KuudraCommand());
        getCommand("mayor").setExecutor(new MayorCommand());
        getCommand("pets").setExecutor(new PetsCommand());
        getCommand("skills").setExecutor(new SkillsCommand());
        SlayerCommand slayerCommand = new SlayerCommand();
        getCommand("slayer").setExecutor(slayerCommand);
        if (getCommand("slay") != null) {
            getCommand("slay").setExecutor(slayerCommand);
        }
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
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
