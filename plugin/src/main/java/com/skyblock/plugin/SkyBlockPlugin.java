package com.skyblock.plugin;

import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.bank.BankManager;
import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.collections.CollectionsManager;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.minion.MinionCommand;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.dungeons.DungeonManager;
import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.command.auctionhouse.AuctionHouseCommand;
import com.skyblock.plugin.command.dungeon.DungeonCommand;
import com.skyblock.plugin.command.fairy.FairyCommand;
import com.skyblock.plugin.commands.BankCommand;
import com.skyblock.plugin.commands.BazaarCommand;
import com.skyblock.plugin.commands.IslandCommand;
import com.skyblock.plugin.commands.KuudraCommand;
import com.skyblock.plugin.commands.MayorCommand;
import com.skyblock.plugin.commands.ProfileCommand;
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
        AuctionHouseManager.getInstance().load(getDataFolder());
        BankManager.getInstance().load(getDataFolder());
        CollectionsManager.getInstance().load(getDataFolder());
        GardenManager.getInstance().load(getDataFolder());
        com.skyblock.slayer.SlayerManager.getInstance().load(getDataFolder());
        KuudraManager.getInstance().load(getDataFolder());
        ProfileManager.getInstance().load(getDataFolder());
        EnchantingManager.getInstance().load(getDataFolder());
        BazaarManager.getInstance().load(getDataFolder());
        FairyManager.getInstance().load(getDataFolder());
        MinionManager.getInstance().load(getDataFolder());
        WardrobeManager.getInstance().load(getDataFolder());
        AlchemyManager.getInstance().load(getDataFolder());
        getCommand("skyblock").setExecutor(new SkyblockMenuCommand());
        getCommand("bank").setExecutor(new BankCommand());
        getCommand("mayor").setExecutor(new MayorCommand());
        getCommand("island").setExecutor(new IslandCommand());
        getCommand("auctionhouse").setExecutor(new AuctionHouseCommand());
        getCommand("kuudra").setExecutor(new KuudraCommand());
        getCommand("bazaar").setExecutor(new BazaarCommand());
        getCommand("profile").setExecutor(new ProfileCommand());
        getCommand("dungeon").setExecutor(new DungeonCommand());
        getCommand("fairy").setExecutor(new FairyCommand());
        getCommand("minion").setExecutor(new MinionCommand());
        getCommand("wardrobe").setExecutor(new WardrobeCommand());
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        CollectionsManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        com.skyblock.slayer.SlayerManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        FairyManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().save(getDataFolder());
        WardrobeManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
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
