package com.skyblock.core;

import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.alchemy.AlchemyListener;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.armorset.ArmorSetListener;
import com.skyblock.core.armorset.ArmorSetManager;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.auction.AuctionCommand;
import com.skyblock.core.auction.AuctionHouseCommand;
import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.auction.AuctionManager;
import com.skyblock.core.bank.BankingCommand;
import com.skyblock.core.bank.BankingManager;
import com.skyblock.core.bank.BankManager;
import com.skyblock.core.island.IslandCommand;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.island.IslandUpgradeCommand;
import com.skyblock.core.island.IslandUpgradeManager;
import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.collection.CollectionListener;
import com.skyblock.core.collection.CollectionManager;
import com.skyblock.core.combat.CombatListener;
import com.skyblock.core.combat.CombatManager;
import com.skyblock.core.combat.StatManager;
import com.skyblock.core.command.BankCommand;
import com.skyblock.core.command.BazaarCommand;
import com.skyblock.core.collection.CollectionCommand;
import com.skyblock.core.command.DungeonCommand;
import com.skyblock.core.command.ProfileCommand;
import com.skyblock.core.command.LeaderboardCommand;
import com.skyblock.core.command.PetsCommand;
import com.skyblock.core.command.ShopCommand;
import com.skyblock.core.command.SkyBlockMenuCommand;
import com.skyblock.core.command.SkillsCommand;
import com.skyblock.core.command.WarpCommand;
import com.skyblock.core.crafting.CraftingCommand;
import com.skyblock.core.crafting.CraftingListener;
import com.skyblock.core.crafting.CraftingManager;
import com.skyblock.core.crafting.SkyBlockCraftingManager;
import com.skyblock.core.crafting.SkyBlockRecipeManager;
import com.skyblock.core.dungeon.DungeonManager;
import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.enchant.EnchantCommand;
import com.skyblock.core.enchant.EnchantmentCommand;
import com.skyblock.core.enchant.EnchantmentListener;
import com.skyblock.core.enchant.SkyBlockEnchantListener;
import com.skyblock.core.enchant.SkyBlockEnchantFacade;
import com.skyblock.core.enchant.SkyBlockEnchantManager;
import com.skyblock.core.enchanting.EnchantingCommand;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.enchanting.EnchantmentManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.forge.ForgeCommand;
import com.skyblock.core.forge.ForgeManager;
import com.skyblock.core.itemforge.ItemForgeManager;
import com.skyblock.core.listener.CoreListeners;
import com.skyblock.core.magic.FairySoulManager;
import com.skyblock.core.menu.MenuListener;
import com.skyblock.core.menu.MenuManager;
import com.skyblock.core.minion.MinionCommand;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.pet.PetCommand;
import com.skyblock.core.pets.PetManager;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.npc.NPCListener;
import com.skyblock.core.npc.NPCManager;
import com.skyblock.core.npc.NpcCommand;
import com.skyblock.core.npc.NpcManager;
import com.skyblock.core.quest.QuestCommand;
import com.skyblock.core.quest.QuestManager;
import com.skyblock.core.scoreboard.ScoreboardManager;
import com.skyblock.core.shop.ShopManager;
import com.skyblock.core.skill.SkillCommand;
import com.skyblock.core.skills.SkillManager;
import com.skyblock.core.stats.CombatStatsListener;
import com.skyblock.core.stats.CombatStatsManager;
import com.skyblock.core.storage.StorageCommand;
import com.skyblock.core.storage.StorageManager;
import com.skyblock.core.storage.YamlPlayerStorage;
import com.skyblock.core.stat.StatListener;
import com.skyblock.core.talisman.TalismanCommand;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.core.warps.WarpManager;
import com.skyblock.core.foraging.ForagingListener;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.mining.MiningListener;
import com.skyblock.core.mining.MiningManager;
import com.skyblock.core.farming.FarmingListener;
import com.skyblock.core.farming.FarmingManager;
import com.skyblock.core.mob.CustomMobListener;
import com.skyblock.core.mob.CustomMobManager;
import com.skyblock.core.mob.MobLootListener;
import com.skyblock.core.mob.MobLootManager;
import com.skyblock.core.mob.MobManager;
import com.skyblock.core.notification.NotificationListener;
import com.skyblock.core.notification.NotificationManager;
import com.skyblock.core.reward.DailyRewardCommand;
import com.skyblock.core.reward.DailyRewardManager;
import com.skyblock.core.trade.TradeCommand;
import com.skyblock.core.trade.TradeManager;
import com.skyblock.core.bestiary.BestiaryCommand;
import com.skyblock.core.bestiary.BestiaryManager;
import com.skyblock.core.party.PartyCommand;
import com.skyblock.core.party.PartyManager;
import com.skyblock.core.slayer.SlayerCommand;
import com.skyblock.core.slayer.SlayerManager;
import com.skyblock.core.accessory.AccessoryBagCommand;
import com.skyblock.core.accessory.AccessoryBagManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
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
        MobManager mobManager = MobManager.getInstance();
        mobManager.init(this);
        MiningManager miningManager = MiningManager.getInstance();
        FarmingManager farmingManager = FarmingManager.getInstance();
        ForagingManager foragingManager = ForagingManager.getInstance();

        // initialise all singletons so they are ready before commands fire
        EconomyManager economyManager = EconomyManager.getInstance();
        SkillManager skillManager = SkillManager.getInstance();
        AuctionHouseManager auctionHouseManager = AuctionHouseManager.getInstance();
        AuctionHouseCommand auctionHouseCommand = new AuctionHouseCommand(auctionHouseManager);
        getCommand("auctionhouse").setExecutor(auctionHouseCommand);
        getCommand("auctionhouse").setTabCompleter(auctionHouseCommand);
        AuctionManager auctionManager = AuctionManager.getInstance();
        getCommand("auction").setExecutor(new AuctionCommand(auctionManager));
        BazaarManager bazaarManager = BazaarManager.getInstance();
        getCommand("bazaar").setExecutor(new BazaarCommand(bazaarManager));
        BankManager bankManager = BankManager.getInstance();
        getCommand("bank").setExecutor(new BankCommand(bankManager));
        BankingManager bankingManager = BankingManager.getInstance();
        BankingCommand bankingCommand = new BankingCommand(bankingManager);
        getCommand("banking").setExecutor(bankingCommand);
        getCommand("banking").setTabCompleter(bankingCommand);
        CollectionManager collectionManager = CollectionManager.getInstance();
        getCommand("collection").setExecutor(new CollectionCommand(collectionManager));
        DungeonManager dungeonManager = DungeonManager.getInstance();
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager));
        SkyBlockEnchantFacade.getInstance();
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        getCommand("enchanting").setExecutor(new EnchantingCommand(enchantingManager));
        com.skyblock.core.enchant.EnchantManager enchantManager = com.skyblock.core.enchant.EnchantManager.getInstance();
        EnchantCommand enchantCommand = new EnchantCommand(enchantManager);
        getCommand("enchant").setExecutor(enchantCommand);
        getCommand("enchant").setTabCompleter(enchantCommand);
        SkyBlockEnchantFacade enchantmentManager = SkyBlockEnchantFacade.getInstance();
        EnchantmentCommand enchantmentCommand = new EnchantmentCommand(enchantmentManager);
        getCommand("enchantment").setExecutor(enchantmentCommand);
        getCommand("enchantment").setTabCompleter(enchantmentCommand);
        FairySoulManager.getInstance();
        ForgeManager forgeManager = ForgeManager.getInstance();
        getCommand("forge").setExecutor(new ForgeCommand(forgeManager));
        ItemForgeManager itemForgeManager = ItemForgeManager.getInstance();
        com.skyblock.core.itemforge.ForgeCommand itemForgeCommand =
                new com.skyblock.core.itemforge.ForgeCommand(itemForgeManager);
        getCommand("itemforge").setExecutor(itemForgeCommand);
        getCommand("itemforge").setTabCompleter(itemForgeCommand);
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        AlchemyManager alchemyManager = AlchemyManager.getInstance();
        getCommand("alchemy").setExecutor(new AlchemyCommand(alchemyManager));
        MinionManager minionManager = MinionManager.getInstance();
        getCommand("minion").setExecutor(new MinionCommand(minionManager));
        getServer().getPluginManager().registerEvents(new com.skyblock.core.minion.MinionListener(minionManager), this);
        PetManager petManager = PetManager.getInstance();
        getCommand("pets").setExecutor(new PetsCommand(petManager));
        com.skyblock.core.pet.PetManager petManagerV2 = com.skyblock.core.pet.PetManager.getInstance();
        PetCommand petCommand = new PetCommand(petManagerV2);
        getCommand("pet").setExecutor(petCommand);
        getCommand("pet").setTabCompleter(petCommand);
        ProfileManager profileManager = ProfileManager.getInstance();
        getCommand("profile").setExecutor(new ProfileCommand(profileManager));
        NpcManager npcManager = NpcManager.getInstance();
        getCommand("npc").setExecutor(new NpcCommand(npcManager, economyManager));
        getServer().getPluginManager().registerEvents(new NPCListener(npcManager), this);
        QuestCommand questCommand = new QuestCommand(QuestManager.getInstance());
        getCommand("quest").setExecutor(questCommand);
        getCommand("quest").setTabCompleter(questCommand);
        ScoreboardManager.getInstance().start(this);
        ShopManager shopManager = ShopManager.getInstance();
        SkyBlockRecipeManager.getInstance();
        SkyBlockCraftingManager skyBlockCraftingManager = SkyBlockCraftingManager.getInstance();
        skyBlockCraftingManager.init(this);
        CraftingManager craftingManager = CraftingManager.getInstance();
        getCommand("crafting").setExecutor(new CraftingCommand(craftingManager));
        getServer().getPluginManager().registerEvents(new CraftingListener(craftingManager), this);
        com.skyblock.core.stat.StatManager statManager = com.skyblock.core.stat.StatManager.getInstance();
        getServer().getPluginManager().registerEvents(new StatListener(statManager), this);
        TalismanManager talismanManager = TalismanManager.getInstance();
        getCommand("talisman").setExecutor(new TalismanCommand(talismanManager));
        WarpManager.getInstance();
        FishingManager fishingManager = FishingManager.getInstance();
        YamlPlayerStorage.getInstance();
        StorageManager storageManager = StorageManager.getInstance();
        StorageCommand storageCommand = new StorageCommand(storageManager);
        getCommand("storage").setExecutor(storageCommand);
        getCommand("storage").setTabCompleter(storageCommand);

        TradeManager tradeManager = TradeManager.getInstance();
        TradeCommand tradeCommand = new TradeCommand(tradeManager);
        getCommand("trade").setExecutor(tradeCommand);
        getCommand("trade").setTabCompleter(tradeCommand);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.trade.TradeListener(tradeManager), this);

        WardrobeManager wardrobeManager = WardrobeManager.getInstance();
        WardrobeCommand wardrobeCommand = new WardrobeCommand(wardrobeManager);
        getCommand("wardrobe").setExecutor(wardrobeCommand);
        getCommand("wardrobe").setTabCompleter(wardrobeCommand);

        IslandManager.getInstance();
        getCommand("island").setExecutor(new IslandCommand(IslandManager.getInstance()));
        IslandUpgradeManager islandUpgradeManager = IslandUpgradeManager.getInstance();
        IslandUpgradeCommand islandUpgradeCommand = new IslandUpgradeCommand(islandUpgradeManager, IslandManager.getInstance(), economyManager);
        getCommand("islandupgrade").setExecutor(islandUpgradeCommand);
        getCommand("islandupgrade").setTabCompleter(islandUpgradeCommand);
        getCommand("skyblock").setExecutor(new SkyBlockMenuCommand(MenuManager.getInstance()));
        getCommand("skills").setExecutor(new SkillsCommand(skillManager));
        com.skyblock.core.skill.SkillManager skillManagerV2 = com.skyblock.core.skill.SkillManager.getInstance();
        SkillCommand skillCommand = new SkillCommand(skillManagerV2);
        getCommand("skill").setExecutor(skillCommand);
        getCommand("skill").setTabCompleter(skillCommand);
        com.skyblock.core.warp.WarpManager warpManager = com.skyblock.core.warp.WarpManager.getInstance();
        warpManager.load(new java.io.File(getDataFolder(), "warps.yml"));
        getCommand("warp").setExecutor(new WarpCommand(warpManager));
        getCommand("shop").setExecutor(new ShopCommand(shopManager, economyManager));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(leaderboardManager));
        DailyRewardManager dailyRewardManager = DailyRewardManager.getInstance();
        DailyRewardCommand dailyRewardCommand = new DailyRewardCommand(dailyRewardManager, economyManager);
        getCommand("dailyreward").setExecutor(dailyRewardCommand);
        getCommand("dailyreward").setTabCompleter(dailyRewardCommand);
        getServer().getPluginManager().registerEvents(new CollectionListener(collectionManager), this);
        getServer().getPluginManager().registerEvents(new MiningListener(miningManager), this);
        getServer().getPluginManager().registerEvents(new ForagingListener(foragingManager), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(farmingManager), this);
        getServer().getPluginManager().registerEvents(new AlchemyListener(alchemyManager), this);
        getServer().getPluginManager().registerEvents(new CombatListener(CombatManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.fishing.FishingListener(fishingManager), this);
        getServer().getPluginManager().registerEvents(new MenuListener(MenuManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new CoreListeners(playerDataManager), this);
        CombatStatsManager combatStatsManager = CombatStatsManager.getInstance();
        getServer().getPluginManager().registerEvents(new CombatStatsListener(combatStatsManager), this);
        getServer().getPluginManager().registerEvents(new EnchantmentListener(SkyBlockEnchantManager.getInstance()), this);
        MobLootManager mobLootManager = MobLootManager.getInstance();
        getServer().getPluginManager().registerEvents(new MobLootListener(mobLootManager), this);
        CustomMobManager customMobManager = CustomMobManager.getInstance();
        getServer().getPluginManager().registerEvents(new CustomMobListener(customMobManager), this);
        ArmorSetManager armorSetManager = ArmorSetManager.getInstance();
        getServer().getPluginManager().registerEvents(new ArmorSetListener(armorSetManager), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.talisman.TalismanListener(talismanManager), this);
        NotificationManager notificationManager = NotificationManager.getInstance();
        getServer().getPluginManager().registerEvents(new NotificationListener(notificationManager), this);
        BestiaryManager bestiaryManager = BestiaryManager.getInstance();
        BestiaryCommand bestiaryCommand = new BestiaryCommand(bestiaryManager);
        getCommand("bestiary").setExecutor(bestiaryCommand);
        getCommand("bestiary").setTabCompleter(bestiaryCommand);

        PartyManager partyManager = PartyManager.getInstance();
        PartyCommand partyCommand = new PartyCommand(partyManager);
        getCommand("party").setExecutor(partyCommand);
        getCommand("party").setTabCompleter(partyCommand);

        AccessoryBagManager accessoryBagManager = AccessoryBagManager.getInstance();
        AccessoryBagCommand accessoryBagCommand = new AccessoryBagCommand(accessoryBagManager);
        getCommand("accessorybag").setExecutor(accessoryBagCommand);
        getCommand("accessorybag").setTabCompleter(accessoryBagCommand);

        SlayerManager slayerManager = SlayerManager.getInstance();
        SlayerCommand slayerCommand = new SlayerCommand(slayerManager);
        getCommand("slay").setExecutor(slayerCommand);
        getCommand("slay").setTabCompleter(slayerCommand);

        com.skyblock.core.enchantment.EnchantmentManager.getInstance();
        com.skyblock.core.enchant.EnchantmentManager.getInstance();
        com.skyblock.core.enchanting.EnchantmentManager.getInstance();
        com.skyblock.core.collections.CollectionManager.getInstance();
        com.skyblock.core.quests.QuestManager.getInstance();
        NPCManager.getInstance();
        getServer().getPluginManager().registerEvents(new SkyBlockEnchantListener(SkyBlockEnchantManager.getInstance()), this);

        getLogger().info("SkyBlock core enabled.");
    }

    @Override
    public void onDisable() {
        try {
            com.skyblock.core.warp.WarpManager.getInstance().save(
                    new java.io.File(getDataFolder(), "warps.yml"));
        } catch (java.io.IOException e) {
            getLogger().warning("Failed to save warps: " + e.getMessage());
        }
        ScoreboardManager.getInstance().stop();
        getLogger().info("SkyBlock core disabled.");
        instance = null;
    }
}
