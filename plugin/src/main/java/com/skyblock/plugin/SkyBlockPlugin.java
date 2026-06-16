package com.skyblock.plugin;

import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.bank.manager.BankManager;
import com.skyblock.core.bazaar.manager.BazaarManager;
import com.skyblock.core.collections.manager.CollectionManager;
import com.skyblock.core.shop.manager.ShopManager;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.plugin.managers.FishingManager;
import com.skyblock.core.enchant.manager.EnchantmentManager;
import com.skyblock.plugin.managers.HOTMManager;
import com.skyblock.core.fairy.FairyManager;
import com.skyblock.plugin.managers.GardenManager;
import com.skyblock.plugin.managers.KuudraManager;
import com.skyblock.plugin.managers.MayorManager;
import com.skyblock.core.minion.manager.MinionManager;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.plugin.managers.WarpManager;
import com.skyblock.core.dungeon.manager.DungeonManager;
import com.skyblock.core.items.manager.CustomItemManager;
import com.skyblock.plugin.commands.AlchemyCommand;
import com.skyblock.core.auction.command.AuctionHouseCommand;
import com.skyblock.plugin.managers.CooldownManager;
import com.skyblock.core.manager.CraftingManager;
import com.skyblock.plugin.managers.EventManager;
import com.skyblock.core.island.manager.IslandManager;
import com.skyblock.plugin.managers.NetworkManager;
import com.skyblock.core.quest.manager.QuestManager;
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
import com.skyblock.plugin.commands.EventCommand;
import com.skyblock.plugin.commands.FishingCommand;
import com.skyblock.plugin.commands.GardenCommand;
import com.skyblock.plugin.commands.HOTMCommand;
import com.skyblock.plugin.commands.HubCommand;
import com.skyblock.core.island.command.IslandCommand;
import com.skyblock.plugin.commands.KuudraCommand;
import com.skyblock.plugin.commands.MayorCommand;
import com.skyblock.core.pets.command.PetsCommand;
import com.skyblock.core.command.ProfileCommand;
import com.skyblock.core.quest.command.QuestCommand;
import com.skyblock.plugin.commands.SkillsCommand;
import com.skyblock.plugin.commands.SlayerCommand;
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
        com.skyblock.core.manager.ProfileManager.getInstance().load(getDataFolder());
        GardenManager.getInstance().load(getDataFolder());
        com.skyblock.plugin.managers.SlayerManager.getInstance().load(getDataFolder());
        KuudraManager.getInstance().load(getDataFolder());
        EnchantmentManager.getInstance().load(getDataFolder());
        BazaarManager.getInstance().load(getDataFolder());
        FairyManager.getInstance().load(getDataFolder());
        MinionManager.getInstance().load(getDataFolder());
        WardrobeManager.getInstance().load(getDataFolder());
        AlchemyManager.getInstance().load(getDataFolder());
        FishingManager.getInstance().load(getDataFolder());
        MayorManager.getInstance().load(getDataFolder());
        com.skyblock.core.pets.manager.PetManager.getInstance().load(getDataFolder());
        HOTMManager.getInstance().load(getDataFolder());
        WarpManager.getInstance().load(getDataFolder());
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
        com.skyblock.plugin.item.ItemRegistry.getInstance().load(this);
        if (!new java.io.File(getDataFolder(), "shops.yml").exists() && getResource("shops.yml") != null) {
            saveResource("shops.yml", false);
        }
        ShopManager.getInstance().load(getDataFolder());
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.shop.listener.ShopListener(), this);
        com.skyblock.core.accessory.AccessoryManager.getInstance();

        getServer().getPluginManager().registerEvents(new com.skyblock.core.collections.listener.CollectionListener(CollectionManager.getInstance()), this);
        com.skyblock.core.pets.manager.PetManager.getInstance();
        getCommand("skyblock").setExecutor(new SkyblockMenuCommand());
        getCommand("bank").setExecutor(new BankCommand(BankManager.getInstance()));
        getCommand("mayor").setExecutor(new MayorCommand());
        getCommand("island").setExecutor(new IslandCommand(IslandManager.getInstance()));
        getCommand("auctionhouse").setExecutor(new AuctionHouseCommand(AuctionHouseManager.getInstance()));
        getCommand("kuudra").setExecutor(new KuudraCommand());
        getCommand("bazaar").setExecutor(new BazaarCommand(BazaarManager.getInstance()));
        getCommand("profile").setExecutor(new ProfileCommand(com.skyblock.core.manager.ProfileManager.getInstance()));
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager));
        getCommand("fairy").setExecutor(new FairyCommand());
        getCommand("minion").setExecutor(new MinionCommand(MinionManager.getInstance()));
        getCommand("wardrobe").setExecutor(new WardrobeCommand());
        getCommand("slayer").setExecutor(new SlayerCommand());
        getCommand("fishing").setExecutor(new FishingCommand());
        getCommand("collections").setExecutor(new CollectionsCommand(CollectionManager.getInstance()));
        getCommand("enchanting").setExecutor(new EnchantingCommand());
        getCommand("hotm").setExecutor(new HOTMCommand());
        getCommand("skills").setExecutor(new SkillsCommand());
        getCommand("garden").setExecutor(new GardenCommand());
        getCommand("pets").setExecutor(new PetsCommand(com.skyblock.core.pets.manager.PetManager.getInstance()));
        getCommand("alchemy").setExecutor(new AlchemyCommand());
        getCommand("hub").setExecutor(new HubCommand());
        getCommand("event").setExecutor(new EventCommand());
        getCommand("quest").setExecutor(new QuestCommand(QuestManager.getInstance()));
        getCommand("trade").setExecutor(new TradeCommand(TradeManager.getInstance()));
        com.skyblock.plugin.manager.DamageManager.getInstance().register(this);
        getServer().getPluginManager().registerEvents(com.skyblock.plugin.combat.manager.CombatManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(profileManager, this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.profile.ProfileJoinListener(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.plugin.profile.PlayerDataManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.EnchantingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.HubClickListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.QuestProgressListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.TimeListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.WeatherListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.minion.listener.MinionListener(), this);
        new com.skyblock.plugin.minion.task.MinionTickScheduler(MinionManager.getInstance()).start(this);
        // menus.StorageMenu/PotionBagMenu/QuiverMenu/FishingBagMenu listeners removed — canonical com.skyblock.plugin.gui.menu.* classes handle clicks via core MenuListener
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.fishing.FishingListener(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.plugin.listener.PlayerJoinSetupListener(), this);
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
        // com.skyblock.plugin.menu.FastTravelMenu listener removed — canonical com.skyblock.plugin.gui.menu.FastTravelMenu handles clicks via core MenuListener
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
        // com.skyblock.plugin.menu.AuctionHouseMenu listener removed — canonical com.skyblock.core.menu.AuctionHouseMenu handles clicks via MenuListener
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
        com.skyblock.core.manager.ProfileManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        com.skyblock.plugin.managers.SlayerManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantmentManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        FairyManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().save(getDataFolder());
        WardrobeManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
        FishingManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        com.skyblock.core.pets.manager.PetManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        dungeonManager.save(getDataFolder());
        IslandManager.getInstance().save(getDataFolder());
        com.skyblock.plugin.managers.DungeonManager.getInstance().save(getDataFolder());
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
