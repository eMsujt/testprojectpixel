package com.skyblock.core;

import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.alchemy.AlchemyListener;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.armorset.ArmorSetListener;
import com.skyblock.core.armorset.ArmorSetManager;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.auction.AuctionCommand;
import com.skyblock.core.auction.AuctionHouseCommand;
import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.bank.command.BankingCommand;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.island.IslandUpgradeManager;
import com.skyblock.core.island.command.IslandCommand;
import com.skyblock.core.island.command.IslandUpgradeCommand;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.collection.CollectionListener;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.combat.CombatManager;
import com.skyblock.core.combat.command.CombatCommand;
import com.skyblock.core.combat.listener.CombatListener;
import com.skyblock.core.command.BankCommand;
import com.skyblock.core.command.BazaarCommand;
import com.skyblock.core.collection.command.CollectionCommand;
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
import com.skyblock.core.manager.DungeonManager;
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
import com.skyblock.core.fishing.FishingCommand;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.fishing.TrophyFishingManager;
import com.skyblock.core.forge.ForgeCommand;
import com.skyblock.core.forge.ForgeManager;
import com.skyblock.core.itemforge.ItemForgeManager;
import com.skyblock.core.listener.CoreListeners;
import com.skyblock.core.magic.FairySoulCommand;
import com.skyblock.core.magic.FairySoulManager;
import com.skyblock.core.menu.MenuListener;
import com.skyblock.core.menu.MenuManager;
import com.skyblock.core.menu.SkyBlockCommand;
import com.skyblock.core.menu.SkyBlockMenuManager;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.pet.command.PetCommand;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.ProfileManager;
import com.skyblock.core.npc.NPCCommand;
import com.skyblock.core.npc.NPCListener;
import com.skyblock.core.npc.NPCManager;
import com.skyblock.core.npc.NpcCommand;
import com.skyblock.core.npc.NpcManager;
import com.skyblock.core.quest.QuestCommand;
import com.skyblock.core.quest.QuestManager;
import com.skyblock.core.scoreboard.ScoreboardManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.skills.command.SkillCommand;
import com.skyblock.core.skills.listener.SkillListener;
import com.skyblock.core.skills.SkillManager;
import com.skyblock.core.stats.CombatStatsListener;
import com.skyblock.core.stats.CombatStatsManager;
import com.skyblock.core.stats.PlayerStatManager;
import com.skyblock.core.stats.StatsCommand;
import com.skyblock.core.stats.StatsManager;
import com.skyblock.core.storage.StorageCommand;
import com.skyblock.core.storage.StorageManager;
import com.skyblock.core.storage.YamlPlayerStorage;
import com.skyblock.core.stat.StatListener;
import com.skyblock.core.talisman.TalismanCommand;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.core.warps.WarpManager;
import com.skyblock.core.foraging.ForagingListener;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.mining.MiningCommand;
import com.skyblock.core.mining.MiningListener;
import com.skyblock.core.mining.MiningManager;
import com.skyblock.core.farming.CropMilestoneListener;
import com.skyblock.core.farming.FarmingListener;
import com.skyblock.core.farming.FarmingManager;
import com.skyblock.core.farming.JacobContestCommand;
import com.skyblock.core.farming.JacobContestManager;
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
import com.skyblock.core.accessory.AccessoryCommand;
import com.skyblock.core.accessory.AccessoryManager;
import com.skyblock.core.cooldown.CooldownCommand;
import com.skyblock.core.cooldown.CooldownManager;
import com.skyblock.core.essence.EssenceCommand;
import com.skyblock.core.essence.EssenceManager;
import com.skyblock.core.crimson.CrimsonIsleCommand;
import com.skyblock.core.crimson.CrimsonIsleManager;
import com.skyblock.core.garden.GardenCommand;
import com.skyblock.core.garden.GardenManager;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.backpack.BackpackCommand;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.bossbar.BossBarListener;
import com.skyblock.core.bossbar.BossBarManager;
import com.skyblock.core.guild.GuildCommand;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.hotm.HotmCommand;
import com.skyblock.core.hotm.HotmManager;
import com.skyblock.core.reforge.ReforgeCommand;
import com.skyblock.core.reforge.ReforgeManager;
import com.skyblock.core.achievement.AchievementCommand;
import com.skyblock.core.achievement.AchievementManager;
import com.skyblock.core.hub.SkyblockHubCommand;
import com.skyblock.core.rift.RiftCommand;
import com.skyblock.core.rift.RiftListener;
import com.skyblock.core.rift.RiftManager;
import com.skyblock.core.season.SeasonCommand;
import com.skyblock.core.season.SeasonManager;
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
        MiningCommand miningCommand = new MiningCommand(miningManager);
        getCommand("mining").setExecutor(miningCommand);
        getCommand("mining").setTabCompleter(miningCommand);
        FarmingManager farmingManager = FarmingManager.getInstance();
        JacobContestManager jacobContestManager = JacobContestManager.getInstance();
        JacobContestCommand jacobContestCommand = new JacobContestCommand(jacobContestManager);
        getCommand("jacobcontest").setExecutor(jacobContestCommand);
        getCommand("jacobcontest").setTabCompleter(jacobContestCommand);
        ForagingManager foragingManager = ForagingManager.getInstance();

        // initialise all singletons so they are ready before commands fire
        EconomyManager economyManager = EconomyManager.getInstance();
        EssenceManager essenceManager = EssenceManager.getInstance();
        EssenceCommand essenceCommand = new EssenceCommand(essenceManager);
        getCommand("essence").setExecutor(essenceCommand);
        getCommand("essence").setTabCompleter(essenceCommand);
        SkillManager skillManager = SkillManager.getInstance();
        AuctionHouseManager auctionHouseManager = AuctionHouseManager.getInstance();
        AuctionHouseCommand auctionHouseCommand = new AuctionHouseCommand(auctionHouseManager);
        getCommand("auctionhouse").setExecutor(auctionHouseCommand);
        getCommand("auctionhouse").setTabCompleter(auctionHouseCommand);
        AuctionCommand auctionCommand = new AuctionCommand(com.skyblock.core.manager.AuctionHouseManager.getInstance());
        getCommand("auction").setExecutor(auctionCommand);
        getCommand("auction").setTabCompleter(auctionCommand);
        BazaarManager bazaarManager = BazaarManager.getInstance();
        BazaarCommand bazaarCommand = new BazaarCommand(bazaarManager);
        getCommand("bazaar").setExecutor(bazaarCommand);
        getCommand("bazaar").setTabCompleter(bazaarCommand);
        BankManager bankManager = BankManager.getInstance();
        BankCommand bankCommand = new BankCommand(bankManager);
        getCommand("bank").setExecutor(bankCommand);
        getCommand("bank").setTabCompleter(bankCommand);
        BankingCommand bankingCommand = new BankingCommand(bankManager);
        getCommand("banking").setExecutor(bankingCommand);
        getCommand("banking").setTabCompleter(bankingCommand);
        CollectionManager collectionManager = CollectionManager.getInstance();
        CollectionCommand collectionCommand = new CollectionCommand(collectionManager);
        getCommand("collection").setExecutor(collectionCommand);
        getCommand("collection").setTabCompleter(collectionCommand);
        DungeonManager dungeonManager = DungeonManager.getInstance();
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager));
        SkyBlockEnchantFacade.getInstance();
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        getCommand("enchanting").setExecutor(new EnchantingCommand(enchantingManager));
        com.skyblock.core.enchant.EnchantManager enchantManager = com.skyblock.core.enchant.EnchantManager.getInstance();
        EnchantCommand enchantCommand = new EnchantCommand(enchantManager);
        getCommand("enchant").setExecutor(enchantCommand);
        getCommand("enchant").setTabCompleter(enchantCommand);
        com.skyblock.core.enchant.EnchantmentManager enchantmentManager = com.skyblock.core.enchant.EnchantmentManager.getInstance();
        EnchantmentCommand enchantmentCommand = new EnchantmentCommand(enchantmentManager);
        getCommand("enchantment").setExecutor(enchantmentCommand);
        getCommand("enchantment").setTabCompleter(enchantmentCommand);
        FairySoulManager fairySoulManager = FairySoulManager.getInstance();
        FairySoulCommand fairySoulCommand = new FairySoulCommand(fairySoulManager);
        getCommand("fairysoul").setExecutor(fairySoulCommand);
        getCommand("fairysoul").setTabCompleter(fairySoulCommand);
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
        MinionCommand minionCommand = new MinionCommand(minionManager);
        getCommand("minion").setExecutor(minionCommand);
        getCommand("minion").setTabCompleter(minionCommand);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.minion.MinionListener(minionManager), this);
        PetManager petManager = PetManager.getInstance();
        PetsCommand petsCommand = new PetsCommand(petManager);
        getCommand("pets").setExecutor(petsCommand);
        getCommand("pets").setTabCompleter(petsCommand);
        PetCommand petCommand = new PetCommand(petManager);
        getCommand("pet").setExecutor(petCommand);
        getCommand("pet").setTabCompleter(petCommand);
        ProfileManager profileManager = ProfileManager.getInstance();
        getCommand("profile").setExecutor(new ProfileCommand(profileManager));
        NpcManager npcManager = NpcManager.getInstance();
        NpcCommand npcCommand = new NpcCommand(npcManager, economyManager);
        getCommand("npc").setExecutor(npcCommand);
        getCommand("npc").setTabCompleter(npcCommand);
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
        TrophyFishingManager trophyFishingManager = TrophyFishingManager.getInstance();
        FishingCommand fishingCommand = new FishingCommand(fishingManager, trophyFishingManager);
        getCommand("fishing").setExecutor(fishingCommand);
        getCommand("fishing").setTabCompleter(fishingCommand);
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
        IslandCommand islandCommand = new IslandCommand(IslandManager.getInstance());
        getCommand("island").setExecutor(islandCommand);
        getCommand("island").setTabCompleter(islandCommand);
        IslandUpgradeManager islandUpgradeManager = IslandUpgradeManager.getInstance();
        IslandUpgradeCommand islandUpgradeCommand = new IslandUpgradeCommand(islandUpgradeManager, IslandManager.getInstance(), economyManager);
        getCommand("islandupgrade").setExecutor(islandUpgradeCommand);
        getCommand("islandupgrade").setTabCompleter(islandUpgradeCommand);
        SkyBlockMenuManager skyBlockMenuManager = SkyBlockMenuManager.getInstance();
        SkyblockHubCommand skyBlockCommand = new SkyblockHubCommand(skyBlockMenuManager, "hub");
        getCommand("skyblock").setExecutor(skyBlockCommand);
        getCommand("skyblock").setTabCompleter(skyBlockCommand);
        getCommand("skills").setExecutor(new SkillsCommand(skillManager));
        SkillCommand skillCommand = new SkillCommand(skillManager);
        getCommand("skill").setExecutor(skillCommand);
        getCommand("skill").setTabCompleter(skillCommand);
        getServer().getPluginManager().registerEvents(new SkillListener(skillManager), this);
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
        getServer().getPluginManager().registerEvents(new CropMilestoneListener(farmingManager), this);
        getServer().getPluginManager().registerEvents(new AlchemyListener(alchemyManager), this);
        getServer().getPluginManager().registerEvents(new CombatListener(CombatManager.getInstance()), this);
        CombatCommand combatCommand = new CombatCommand(CombatManager.getInstance());
        getCommand("combat").setExecutor(combatCommand);
        getCommand("combat").setTabCompleter(combatCommand);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.fishing.FishingListener(fishingManager), this);
        getServer().getPluginManager().registerEvents(new MenuListener(MenuManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new CoreListeners(playerDataManager), this);
        CombatStatsManager combatStatsManager = CombatStatsManager.getInstance();
        getServer().getPluginManager().registerEvents(new CombatStatsListener(combatStatsManager), this);
        StatsManager statsManager = StatsManager.getInstance();
        getServer().getPluginManager().registerEvents(new com.skyblock.core.stats.StatListener(statsManager), this);
        StatsCommand statsCommand = new StatsCommand(PlayerStatManager.getInstance());
        getCommand("stats").setExecutor(statsCommand);
        getCommand("stats").setTabCompleter(statsCommand);
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

        AccessoryManager accessoryManager = AccessoryManager.getInstance();
        AccessoryCommand accessoryCommand = new AccessoryCommand(accessoryManager);
        getCommand("accessory").setExecutor(accessoryCommand);
        getCommand("accessory").setTabCompleter(accessoryCommand);

        SlayerManager slayerManager = SlayerManager.getInstance();
        SlayerCommand slayerCommand = new SlayerCommand(slayerManager);
        getCommand("slay").setExecutor(slayerCommand);
        getCommand("slay").setTabCompleter(slayerCommand);

        HotmManager hotmManager = HotmManager.getInstance();
        hotmManager.load(getDataFolder());
        HotmCommand hotmCommand = new HotmCommand(hotmManager);
        getCommand("hotm").setExecutor(hotmCommand);
        getCommand("hotm").setTabCompleter(hotmCommand);

        CrimsonIsleManager crimsonIsleManager = CrimsonIsleManager.getInstance();
        CrimsonIsleCommand crimsonIsleCommand = new CrimsonIsleCommand(crimsonIsleManager);
        getCommand("crimsonisle").setExecutor(crimsonIsleCommand);
        getCommand("crimsonisle").setTabCompleter(crimsonIsleCommand);

        KuudraManager kuudraManager = KuudraManager.getInstance();
        KuudraCommand kuudraCommand = new KuudraCommand(kuudraManager);
        getCommand("kuudra").setExecutor(kuudraCommand);
        getCommand("kuudra").setTabCompleter(kuudraCommand);

        GardenManager gardenManager = GardenManager.getInstance();
        GardenCommand gardenCommand = new GardenCommand(gardenManager);
        getCommand("garden").setExecutor(gardenCommand);
        getCommand("garden").setTabCompleter(gardenCommand);

        GuildManager guildManager = GuildManager.getInstance();
        GuildCommand guildCommand = new GuildCommand(guildManager);
        getCommand("guild").setExecutor(guildCommand);
        getCommand("guild").setTabCompleter(guildCommand);

        BackpackManager backpackManager = BackpackManager.getInstance();
        BackpackCommand backpackCommand = new BackpackCommand(backpackManager);
        getCommand("backpack").setExecutor(backpackCommand);
        getCommand("backpack").setTabCompleter(backpackCommand);

        BossBarManager bossBarManager = BossBarManager.getInstance();
        getServer().getPluginManager().registerEvents(new BossBarListener(bossBarManager), this);

        ReforgeManager reforgeManager = ReforgeManager.getInstance();
        ReforgeCommand reforgeCommand = new ReforgeCommand(reforgeManager);
        getCommand("reforge").setExecutor(reforgeCommand);
        getCommand("reforge").setTabCompleter(reforgeCommand);

        RiftManager riftManager = RiftManager.getInstance();
        RiftCommand riftCommand = new RiftCommand(riftManager);
        getCommand("rift").setExecutor(riftCommand);
        getCommand("rift").setTabCompleter(riftCommand);
        getServer().getPluginManager().registerEvents(new RiftListener(riftManager), this);

        com.skyblock.core.enchantment.EnchantmentManager.getInstance();
        com.skyblock.core.enchantment.SkyBlockEnchantManager skyBlockEnchantManager =
                com.skyblock.core.enchantment.SkyBlockEnchantManager.getInstance();
        com.skyblock.core.enchantment.EnchantCommand enchantmentEnchantCommand =
                new com.skyblock.core.enchantment.EnchantCommand(skyBlockEnchantManager);
        getCommand("skyblockenchant").setExecutor(enchantmentEnchantCommand);
        getCommand("skyblockenchant").setTabCompleter(enchantmentEnchantCommand);
        com.skyblock.core.enchant.EnchantmentManager.getInstance();
        com.skyblock.core.enchanting.EnchantmentManager.getInstance();
        CollectionManager.getInstance();
        com.skyblock.core.manager.QuestManager.getInstance();
        NPCCommand npcManagerCommand = new NPCCommand(NPCManager.getInstance());
        getCommand("npcmanager").setExecutor(npcManagerCommand);
        getCommand("npcmanager").setTabCompleter(npcManagerCommand);
        com.skyblock.core.hud.ScoreboardManager.getInstance();
        com.skyblock.core.minions.MinionManager.getInstance();
        com.skyblock.core.manager.PetManager.getInstance();
        com.skyblock.core.skills.SkillsManager.getInstance();
        getServer().getPluginManager().registerEvents(new SkyBlockEnchantListener(SkyBlockEnchantManager.getInstance()), this);

        AchievementManager achievementManager = AchievementManager.getInstance();
        AchievementCommand achievementCommand = new AchievementCommand(achievementManager);
        getCommand("achievement").setExecutor(achievementCommand);
        getCommand("achievement").setTabCompleter(achievementCommand);

        SeasonManager seasonManager = SeasonManager.getInstance();
        SeasonCommand seasonCommand = new SeasonCommand(seasonManager);
        getCommand("season").setExecutor(seasonCommand);
        getCommand("season").setTabCompleter(seasonCommand);

        CooldownManager cooldownManager = CooldownManager.getInstance();
        CooldownCommand cooldownCommand = new CooldownCommand(cooldownManager);
        getCommand("cooldown").setExecutor(cooldownCommand);
        getCommand("cooldown").setTabCompleter(cooldownCommand);

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
        HotmManager.getInstance().save(getDataFolder());
        ScoreboardManager.getInstance().stop();
        getLogger().info("SkyBlock core disabled.");
        instance = null;
    }
}
