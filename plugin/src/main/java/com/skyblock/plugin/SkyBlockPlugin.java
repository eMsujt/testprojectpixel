package com.skyblock.plugin;

import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.plugin.managers.FishingManager;
import com.skyblock.core.manager.EnchantmentManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.core.garden.GardenCommand;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.hotm.command.HOTMCommand;
import com.skyblock.core.hotm.manager.HOTMManager;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.mayor.MayorCommand;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.slayer.SlayerCommand;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.plugin.managers.WarpManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.items.manager.CustomItemManager;
import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.auction.command.AuctionHouseCommand;
import com.skyblock.plugin.managers.CooldownManager;
import com.skyblock.core.manager.CraftingManager;
import com.skyblock.plugin.managers.EventManager;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.plugin.managers.NetworkManager;
import com.skyblock.core.manager.QuestManager;
import com.skyblock.plugin.managers.TradingManager;
import com.skyblock.plugin.managers.TimeManager;
import com.skyblock.plugin.managers.WeatherManager;
import com.skyblock.plugin.hud.ActionBarManager;
import com.skyblock.core.dungeon.command.DungeonCommand;
import com.skyblock.plugin.command.fairy.FairyCommand;
import com.skyblock.core.bank.command.BankCommand;
import com.skyblock.core.bazaar.command.BazaarCommand;
import com.skyblock.core.collections.command.CollectionsCommand;
import com.skyblock.plugin.commands.EnchantingCommand;
import com.skyblock.core.event.EventCommand;
import com.skyblock.plugin.commands.FishingCommand;
import com.skyblock.plugin.commands.HubCommand;
import com.skyblock.islands.command.IslandCommand;
import com.skyblock.core.pet.PetCommand;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.command.ProfileCommand;
import com.skyblock.core.quest.command.QuestCommand;
import com.skyblock.core.trade.TradeCommand;
import com.skyblock.core.trade.TradeManager;
import com.skyblock.core.command.SkyblockMenuCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the SkyBlock plugin.
 *
 * <p>Holds the singleton plugin instance, owns the manager services for the
 * plugin's systems and drives the enable/disable lifecycle.</p>
 */
public final class SkyBlockPlugin extends JavaPlugin {

    private static SkyBlockPlugin instance;

    private DungeonManager dungeonManager;
    private CustomItemManager itemManager;
    private com.skyblock.plugin.profile.ProfileManager profileManager;

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
        dungeonManager = DungeonManager.getInstance();
        dungeonManager.load(getDataFolder());
        itemManager = new CustomItemManager();
        profileManager = com.skyblock.plugin.profile.ProfileManager.getInstance();
        profileManager.init(this);
        AuctionHouseManager.getInstance().load(getDataFolder());
        BankManager.getInstance().load(getDataFolder());
        SkillManager.getInstance().load(getDataFolder());
        CollectionManager.getInstance().load(getDataFolder());
        com.skyblock.core.profile.manager.ProfileManager.getInstance().load(getDataFolder());
        EnchantmentManager.getInstance().load(getDataFolder());
        BazaarManager.getInstance().load(getDataFolder());
        FairyManager.getInstance().load(getDataFolder());
        MinionManager.getInstance().load(getDataFolder());
        WardrobeManager.getInstance().load(getDataFolder());
        AlchemyManager.getInstance().load(getDataFolder());
        FishingManager.getInstance().load(getDataFolder());
        PetManager.getInstance().load(getDataFolder());
        MayorManager.getInstance().load(getDataFolder());
        KuudraManager.getInstance().load(getDataFolder());
        SlayerManager.getInstance().load(getDataFolder());
        HOTMManager.getInstance().load(getDataFolder());
        GardenManager.getInstance().load(getDataFolder());
        WarpManager.getInstance().load(getDataFolder());
        IslandManager.getInstance().load(getDataFolder());
        dungeonManager.load(getDataFolder());
        CraftingManager.getInstance().registerRecipes(this);
        CooldownManager.getInstance();
        EventManager.getInstance().load(getDataFolder());
        QuestManager.getInstance().load(getDataFolder());
        TradingManager.getInstance();
        WeatherManager.getInstance().load(getDataFolder());
        TimeManager.getInstance().load(getDataFolder());
        NetworkManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.items.ItemManager.getInstance().load(this);
        com.skyblock.plugin.item.ItemRegistry.getInstance().load(this);
        if (!new java.io.File(getDataFolder(), "shops.yml").exists() && getResource("shops.yml") != null) {
            saveResource("shops.yml", false);
        }
        ShopManager.getInstance().load(getDataFolder());
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.shop.listener.ShopListener(), this);
        com.skyblock.core.manager.AccessoryManager.getInstance();

        getServer().getPluginManager().registerEvents(new com.skyblock.core.collections.listener.CollectionListener(CollectionManager.getInstance()), this);
        PetManager.getInstance();
        getCommand("skyblock").setExecutor(new SkyblockMenuCommand());
        getCommand("bank").setExecutor(new BankCommand(BankManager.getInstance()));
        MayorCommand mayorCmd = new MayorCommand(MayorManager.getInstance());
        getCommand("mayor").setExecutor(mayorCmd);
        getCommand("mayor").setTabCompleter(mayorCmd);
        getCommand("island").setExecutor(new IslandCommand(IslandManager.getInstance()));
        getCommand("auctionhouse").setExecutor(new AuctionHouseCommand(AuctionHouseManager.getInstance()));
        KuudraCommand kuudraCmd = new KuudraCommand(KuudraManager.getInstance());
        getCommand("kuudra").setExecutor(kuudraCmd);
        getCommand("kuudra").setTabCompleter(kuudraCmd);
        getCommand("bazaar").setExecutor(new BazaarCommand(BazaarManager.getInstance()));
        getCommand("profile").setExecutor(new ProfileCommand(com.skyblock.core.profile.manager.ProfileManager.getInstance()));
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager));
        getCommand("fairy").setExecutor(new FairyCommand());
        getCommand("minion").setExecutor(new MinionCommand(MinionManager.getInstance()));
        getCommand("wardrobe").setExecutor(new WardrobeCommand());
        SlayerCommand slayerCmd = new SlayerCommand(SlayerManager.getInstance());
        getCommand("slayer").setExecutor(slayerCmd);
        getCommand("slayer").setTabCompleter(slayerCmd);
        getCommand("fishing").setExecutor(new FishingCommand());
        getCommand("collections").setExecutor(new CollectionsCommand(CollectionManager.getInstance()));
        getCommand("enchanting").setExecutor(new EnchantingCommand());
        HOTMCommand hotmCmd = new HOTMCommand(HOTMManager.getInstance());
        getCommand("hotm").setExecutor(hotmCmd);
        getCommand("hotm").setTabCompleter(hotmCmd);
        GardenCommand gardenCmd = new GardenCommand(GardenManager.getInstance());
        getCommand("garden").setExecutor(gardenCmd);
        getCommand("garden").setTabCompleter(gardenCmd);
        PetCommand petCommand = new PetCommand(PetManager.getInstance());
        getCommand("pets").setExecutor(petCommand);
        getCommand("pet").setExecutor(petCommand);
        getCommand("alchemy").setExecutor(new AlchemyCommand(AlchemyManager.getInstance()));
        getCommand("hub").setExecutor(new HubCommand());
        getCommand("event").setExecutor(new EventCommand(com.skyblock.core.manager.EventManager.getInstance()));
        getCommand("quest").setExecutor(new QuestCommand(QuestManager.getInstance()));
        getCommand("trade").setExecutor(new TradeCommand(TradeManager.getInstance()));
        com.skyblock.plugin.manager.DamageManager.getInstance().register(this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.combat.listener.CombatListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(profileManager, this);
        getServer().getPluginManager().registerEvents(com.skyblock.plugin.profile.PlayerDataManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.EnchantingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.QuestProgressListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.TimeListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.WeatherListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.minion.listener.MinionListener(), this);
        new com.skyblock.plugin.minion.task.MinionTickScheduler(MinionManager.getInstance()).start(this);
        // menus.StorageMenu/PotionBagMenu/QuiverMenu/FishingBagMenu listeners removed — canonical com.skyblock.gui.menu.* classes handle clicks via core MenuListener
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.fishing.FishingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.minion.listener.MinionPlacementListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.FarmingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.ForagingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.ForagingXpListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.MiningXpListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.FishingXpListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.ForagingFishingXpListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.TamingXpListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.menu.listener.MenuListener(com.skyblock.core.menu.manager.MenuManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.CombatListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.AlchemyListener(), this);
        // com.skyblock.plugin.economy.BazaarMenu listener removed — canonical com.skyblock.core.menu.BazaarMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.BankMenu listener removed — canonical com.skyblock.core.menu.BankMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.QuestsMenu listener removed — canonical com.skyblock.core.menu.QuestsMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.CollectionsMenu listener removed — canonical com.skyblock.core.menu.CollectionsMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.FastTravelMenu listener removed — canonical com.skyblock.gui.menu.FastTravelMenu handles clicks via core MenuListener
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.MiningListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.TamingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.FishingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.EnchantingSkillListener(this), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.MiningSkillListener(this), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.ForagingSkillListener(this), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.AlchemySkillListener(this), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.FarmingSkillListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.FishingSkillListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.TamingSkillListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.CarpentryListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.SkyBlockMenuItemListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.SkillProgressionListener(this), this);
        // com.skyblock.plugin.menu.SkyBlockMenu listener removed — canonical com.skyblock.core.menu.SkyBlockMainMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.BazaarMenu listener removed — canonical com.skyblock.core.menu.BazaarMenu handles clicks via MenuListener
        // com.skyblock.plugin.menu.AuctionHouseMenu listener removed — canonical com.skyblock.core.auction.gui.AuctionHouseMenu handles clicks via MenuListener
        new com.skyblock.plugin.minion.task.MinionTickTask(MinionManager.getInstance())
                .runTaskTimer(this, com.skyblock.plugin.minion.task.MinionTickTask.PERIOD_TICKS, com.skyblock.plugin.minion.task.MinionTickTask.PERIOD_TICKS);
        new com.skyblock.plugin.profile.ProfileSaveTask(getDataFolder(), getLogger())
                .runTaskTimerAsynchronously(this, 6000L, 6000L);
        new ActionBarManager().start(this);
        getLogger().info("SkyBlock plugin enabled.");
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        SkillManager.getInstance().save(getDataFolder());
        CollectionManager.getInstance().save(getDataFolder());
        com.skyblock.core.profile.manager.ProfileManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        EnchantmentManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        FairyManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().save(getDataFolder());
        WardrobeManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
        FishingManager.getInstance().save(getDataFolder());
        PetManager.getInstance().save(getDataFolder());
        dungeonManager.save(getDataFolder());
        IslandManager.getInstance().save(getDataFolder());
        try {
            WarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save warp data: " + e.getMessage());
        }
        ShopManager.getInstance().save(getDataFolder());
        EventManager.getInstance().save(getDataFolder());
        QuestManager.getInstance().save(getDataFolder());
        WeatherManager.getInstance().save(getDataFolder());
        TimeManager.getInstance().save(getDataFolder());
        NetworkManager.getInstance().save(getDataFolder());
        getLogger().info("SkyBlock plugin disabled.");
        instance = null;
    }

    /** Returns the dungeon session service. */
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }

    /** Returns the custom item registry. */
    public CustomItemManager getItemManager() {
        return itemManager;
    }

    /** Returns the placed-minion production service. */
    public MinionManager getMinionManager() {
        return MinionManager.getInstance();
    }

    /** Returns the player profile registry. */
    public com.skyblock.plugin.profile.ProfileManager getProfileManager() {
        return profileManager;
    }

}
