package com.skyblock.plugin;

import com.skyblock.plugin.managers.AuctionHouseManager;
import com.skyblock.plugin.managers.AuctionManager;
import com.skyblock.plugin.managers.BankManager;
import com.skyblock.plugin.managers.BazaarManager;
import com.skyblock.plugin.managers.CollectionsManager;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.plugin.managers.FishingManager;
import com.skyblock.plugin.managers.EnchantingManager;
import com.skyblock.plugin.managers.PetsManager;
import com.skyblock.plugin.managers.HOTMManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.plugin.managers.GardenManager;
import com.skyblock.plugin.managers.KuudraManager;
import com.skyblock.plugin.managers.MayorManager;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.minion.MinionCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.plugin.managers.ProfileManager;
import com.skyblock.plugin.managers.SkillsManager;
import com.skyblock.plugin.managers.WarpManager;
import com.skyblock.dungeons.DungeonManager;
import com.skyblock.economy.CoinManager;
import com.skyblock.core.items.CustomItemManager;
import com.skyblock.plugin.commands.AuctionHouseCommand;
import com.skyblock.plugin.managers.CooldownManager;
import com.skyblock.plugin.managers.CraftingManager;
import com.skyblock.plugin.managers.EventManager;
import com.skyblock.plugin.managers.IslandManager;
import com.skyblock.plugin.managers.NetworkManager;
import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.TradingManager;
import com.skyblock.plugin.managers.TimeManager;
import com.skyblock.plugin.managers.WeatherManager;
import com.skyblock.plugin.collections.CollectionsListener;
import com.skyblock.plugin.combat.CombatListener;
import com.skyblock.plugin.hud.ActionBarManager;
import com.skyblock.plugin.listeners.FarmingListener;
import com.skyblock.plugin.command.dungeon.DungeonCommand;
import com.skyblock.plugin.command.fairy.FairyCommand;
import com.skyblock.plugin.commands.BankCommand;
import com.skyblock.plugin.commands.BazaarCommand;
import com.skyblock.plugin.commands.CollectionsCommand;
import com.skyblock.plugin.commands.EnchantingCommand;
import com.skyblock.plugin.commands.FishingCommand;
import com.skyblock.plugin.commands.GardenCommand;
import com.skyblock.plugin.commands.HOTMCommand;
import com.skyblock.plugin.commands.IslandCommand;
import com.skyblock.plugin.commands.KuudraCommand;
import com.skyblock.plugin.commands.MayorCommand;
import com.skyblock.plugin.commands.PetsCommand;
import com.skyblock.plugin.commands.ProfileCommand;
import com.skyblock.plugin.commands.SkillsCommand;
import com.skyblock.plugin.commands.SlayerCommand;
import com.skyblock.plugin.menu.SkyblockMenuCommand;
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
    private CustomItemManager itemManager;

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
        itemManager = new CustomItemManager();
        AuctionHouseManager.getInstance().load(getDataFolder());
        BankManager.getInstance().load(getDataFolder());
        CollectionsManager.getInstance().load(getDataFolder());
        GardenManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.managers.SlayerManager.getInstance().load(getDataFolder());
        KuudraManager.getInstance().load(getDataFolder());
        ProfileManager.getInstance().load(getDataFolder());
        EnchantingManager.getInstance().load(getDataFolder());
        BazaarManager.getInstance().load(getDataFolder());
        AuctionManager.getInstance().load(getDataFolder());
        FairyManager.getInstance().load(getDataFolder());
        MinionManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.minions.MinionManager.getInstance().start(this);
        WardrobeManager.getInstance().load(getDataFolder());
        AlchemyManager.getInstance().load(getDataFolder());
        FishingManager.getInstance().load(getDataFolder());
        MayorManager.getInstance().load(getDataFolder());
        PetsManager.getInstance().load(getDataFolder());
        HOTMManager.getInstance().load(getDataFolder());
        WarpManager.getInstance().load(getDataFolder());
        SkillsManager.getInstance().load(getDataFolder());
        IslandManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.managers.DungeonManager.getInstance().load(getDataFolder());
        CraftingManager.getInstance().registerRecipes(this);
        CooldownManager.getInstance();
        EventManager.getInstance().load(getDataFolder());
        QuestManager.getInstance().load(getDataFolder());
        TradingManager.getInstance();
        WeatherManager.getInstance().load(getDataFolder());
        TimeManager.getInstance().load(getDataFolder());
        NetworkManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.items.ItemManager.getInstance().load(this);
        com.skyblock.plugin.economy.ShopManager.getInstance().load(this);
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
        getCommand("slayer").setExecutor(new SlayerCommand());
        getCommand("fishing").setExecutor(new FishingCommand());
        getCommand("collections").setExecutor(new CollectionsCommand());
        getCommand("enchanting").setExecutor(new EnchantingCommand());
        getCommand("hotm").setExecutor(new HOTMCommand());
        getCommand("skills").setExecutor(new SkillsCommand());
        getCommand("garden").setExecutor(new GardenCommand());
        getCommand("pets").setExecutor(new PetsCommand());
        com.skyblock.plugin.collections.CollectionManager.getInstance();
        getServer().getPluginManager().registerEvents(new FarmingListener(), this);
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
        getServer().getPluginManager().registerEvents(new CollectionsListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.profile.ProfileManager(this), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.skills.SkillsListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.minions.MinionPlacementListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.islands.IslandManager(this), this);
        new ActionBarManager().start(this);
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        CollectionsManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        com.skyblock.plugin.managers.SlayerManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        AuctionManager.getInstance().save(getDataFolder());
        FairyManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().save(getDataFolder());
        WardrobeManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
        FishingManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        PetsManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        SkillsManager.getInstance().save(getDataFolder());
        IslandManager.getInstance().save(getDataFolder());
        com.skyblock.plugin.managers.DungeonManager.getInstance().save(getDataFolder());
        try {
            WarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save warp data: " + e.getMessage());
        }
        EventManager.getInstance().save(getDataFolder());
        QuestManager.getInstance().save(getDataFolder());
        WeatherManager.getInstance().save(getDataFolder());
        TimeManager.getInstance().save(getDataFolder());
        NetworkManager.getInstance().save(getDataFolder());
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

    /** Returns the custom item registry. */
    public CustomItemManager getItemManager() {
        return itemManager;
    }

}
