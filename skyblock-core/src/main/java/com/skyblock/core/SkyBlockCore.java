package com.skyblock.core;

import com.skyblock.core.manager.ActionBarManager;
import com.skyblock.core.manager.AccessoryManager;
import com.skyblock.core.command.QuestCommand;
import com.skyblock.core.crafting.CraftingCommand;
import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.manager.QuestManager;
import com.skyblock.core.auction.command.AuctionHouseCommand;
import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.command.BankCommand;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.command.BazaarCommand;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.command.CalendarCommand;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.museum.MuseumCommand;
import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.command.EssenceCommand;
import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.command.DungeonCommand;
import com.skyblock.core.dungeon.command.DungeonClassCommand;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonClassManager;
import com.skyblock.core.enchanting.EnchantingCommand;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.fishing.command.FishingCommand;
import com.skyblock.core.fishing.listener.TrophyFishListener;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.fairysoul.FairySoulCommand;
import com.skyblock.core.manager.FairySoulManager;
import com.skyblock.core.command.BestiaryCommand;
import com.skyblock.core.manager.HarpCommand;
import com.skyblock.core.manager.TrophyFishCommand;
import com.skyblock.core.command.EnchantCommand;
import com.skyblock.core.command.EnderChestCommand;
import com.skyblock.core.command.MenuCommand;
import com.skyblock.core.menu.GardenMenu;
import com.skyblock.core.menu.DungeonsMenu;
import com.skyblock.core.menu.CollectionsMenu;
import com.skyblock.core.menu.MinionMenu;
import com.skyblock.core.menu.ProfileMenu;
import com.skyblock.core.menu.ForgeMenu;
import com.skyblock.core.menu.SkyBlockMenu;
import com.skyblock.core.menu.PetMenu;
import com.skyblock.core.menu.StatsMenu;
import com.skyblock.core.menu.CrimsonIsleMenu;
import com.skyblock.core.menu.DojoMenu;
import com.skyblock.core.menu.TrophyFishingMenu;
import com.skyblock.core.manager.JerryWorkshopCommand;
import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.HarpManager;
import com.skyblock.core.manager.JerryWorkshopManager;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.guild.GuildCommand;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.command.HOTMCommand;
import com.skyblock.core.manager.HOTMManager;
import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.manager.TamingManager;
import com.skyblock.core.mayor.MayorCommand;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.party.PartyCommand;
import com.skyblock.core.manager.PartyManager;
import com.skyblock.core.command.PetCommand;
import com.skyblock.core.ability.AbilityCommand;
import com.skyblock.core.manager.ItemAbilityManager;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.command.IslandCommand;
import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.command.ReforgeCommand;
import com.skyblock.core.backpack.BackpackCommand;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.event.EventCommand;
import com.skyblock.core.manager.EventManager;
import com.skyblock.core.chat.ChatCommand;
import com.skyblock.core.chat.ChatManager;
import com.skyblock.core.reward.DailyRewardCommand;
import com.skyblock.core.reward.DailyRewardManager;
import com.skyblock.core.leaderboard.LeaderboardCommand;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.mail.MailCommand;
import com.skyblock.core.mail.MailManager;
import com.skyblock.core.command.CollectionsCommand;
import com.skyblock.core.command.CollectionCommand;
import com.skyblock.core.collections.listener.CollectionListener;
import com.skyblock.core.listener.ProgressionListener;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.menu.EnchantingMenu;
import com.skyblock.core.menu.EssenceShopMenu;
import com.skyblock.core.combat.command.CombatCommand;
import com.skyblock.core.combat.manager.CombatManager;
import com.skyblock.core.coop.CoopCommand;
import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.crimson.CrimsonCommand;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.manager.DojoManager;
import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.chocolate.ChocolateFactoryManager;
import com.skyblock.core.menu.MiningCommissionMenu;
import com.skyblock.core.menu.BazaarMenu;
import com.skyblock.core.menu.ChocolateFactoryMenu;
import com.skyblock.core.menu.ForagingMenu;
import com.skyblock.core.menu.JacobsContestMenu;
import com.skyblock.core.manager.JacobsContestManager;
import com.skyblock.core.vault.VaultCommand;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.booster.BoosterCommand;
import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.mailbox.MailboxCommand;
import com.skyblock.core.mailbox.MailboxManager;
import com.skyblock.core.friend.FriendCommand;
import com.skyblock.core.friend.FriendManager;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.manager.RepairManager;
import com.skyblock.core.command.RepairCommand;
import com.skyblock.core.foraging.ForagingCommand;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.manager.MiningManager;
import com.skyblock.core.mining.command.MiningCommand;
import com.skyblock.core.mining.command.MiningZoneCommand;
import com.skyblock.core.mining.manager.MiningZoneManager;
import com.skyblock.core.manager.TradeManager;
import com.skyblock.core.trade.TradeCommand;
import com.skyblock.core.trade.TradeListener;
import com.skyblock.core.achievement.AchievementCommand;
import com.skyblock.core.achievement.AchievementManager;
import com.skyblock.core.level.SkyblockLevelCommand;
import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.menu.manager.SkyBlockMenuManager;
import com.skyblock.core.run.RunCommand;
import com.skyblock.core.dungeon.manager.RunManager;
import com.skyblock.core.title.TitleCommand;
import com.skyblock.core.title.TitleManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.command.SkillsCommand;
import com.skyblock.core.command.SlayerCommand;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.listener.SlayerListener;
import com.skyblock.core.manager.StatCommand;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.command.ProfileCommand;
import com.skyblock.core.command.SackCommand;
import com.skyblock.core.command.ShopCommand;
import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.ShopManager;
import com.skyblock.core.command.SkyBlockMenuCommand;
import com.skyblock.core.command.WarpCommand;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.manager.AlchemyManager;
import com.skyblock.core.command.WardrobeCommand;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.accessory.command.AccessoryBagCommand;
import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.rift.RiftCommand;
import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.crystalhollows.CrystalHollowsCommand;
import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.storage.StorageManager;
import com.skyblock.core.command.StorageCommand;
import com.skyblock.core.npc.NpcManager;
import com.skyblock.core.npc.NpcCommand;
import com.skyblock.core.npc.NPCListener;
import com.skyblock.core.manager.ManaManager;
import com.skyblock.core.manager.PlayerDataManager;
import com.skyblock.core.manager.ScoreboardManager;
import com.skyblock.core.manager.RunecraftingManager;
import com.skyblock.core.manager.SkyBlockEventManager;
import com.skyblock.core.manager.CarpentryManager;
import com.skyblock.core.manager.MobManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyBlockCore extends JavaPlugin {

    private static SkyBlockCore instance;

    // core data
    private SkillManager skillsManager;
    private ProfileManager profile;
    private CollectionManager collectionManager;

    // economy
    private BankManager bankManager;
    private AuctionHouseManager auctionHouseManager;
    private BazaarManager bazaarManager;

    // progression / world content
    private MayorManager mayorManager;
    private CalendarManager calendarManager;
    private EssenceManager essenceManager;
    private EssenceShopManager essenceShopManager;
    private DungeonManager dungeonManager;
    private DungeonClassManager dungeonClassManager;
    private PetManager petManager;
    private HOTMManager hotmManager;
    private SlayerManager slayerManager;
    private TrophyFishManager trophyFishManager;
    private BestiaryManager bestiaryManager;
    private MuseumManager museumManager;
    private RiftManager riftManager;
    private MinionManager minionManager;
    private ForgeManager forgeManager;
    private StatManager statManager;
    private WardrobeManager wardrobeManager;
    private AccessoryBagManager accessoryBagManager;
    private SackManager sackManager;
    private StorageManager storageManager;
    private com.skyblock.core.manager.StorageManager storageCoordinator;

    private IslandManager islandManager;
    private GardenManager gardenManager;
    private CrimsonIsleManager crimsonIsleManager;
    private DojoManager dojoManager;
    private CommissionManager commissionManager;
    private ChocolateFactoryManager chocolateFactoryManager;
    private EnchantingManager enchantingManager;
    private FishingManager fishingManager;
    private GuildManager guildManager;
    private PartyManager partyManager;
    private FairySoulManager fairySoulManager;
    private HarpManager harpManager;
    private JerryWorkshopManager jerryWorkshopManager;
    private KuudraManager kuudraManager;
    private ReforgeManager reforgeManager;
    private CraftingManager craftingManager;
    private QuestManager questManager;
    private TradeManager tradeManager;
    private BackpackManager backpackManager;
    private EventManager eventManager;
    private ForagingManager foragingManager;
    private MiningManager miningManager;
    private CombatManager combatManager;
    private ChatManager chatManager;
    private LeaderboardManager leaderboardManager;
    private MailManager mailManager;
    private CoopManager coopManager;
    private ReputationManager reputationManager;
    private VaultManager vaultManager;
    private FriendManager friendManager;
    private BoosterManager boosterManager;
    private MailboxManager mailboxManager;
    private WarpManager warpManager;
    private AchievementManager achievementManager;
    private SkyblockLevelManager skyblockLevelManager;
    private TitleManager titleManager;
    private RunManager runManager;
    private AlchemyManager alchemyManager;
    private CrystalHollowsManager crystalHollowsManager;
    private JacobsContestManager jacobsContestManager;
    private NpcManager npcManager;
    private RunecraftingManager runecraftingManager;
    private ManaManager manaManager;
    private SkyBlockEventManager skyBlockEventManager;
    private TamingManager tamingManager;
    private CarpentryManager carpentryManager;
    private MobManager mobManager;

    public static SkyBlockCore getInstance() {
        return instance;
    }

    private void initManagers() {
        // core data — loaded first; others may depend on skill/profile state
        skillsManager = SkillManager.getInstance();
        skillsManager.load(getDataFolder());
        profile = ProfileManager.getInstance();
        profile.load(getDataFolder());
        collectionManager = CollectionManager.getInstance();
        collectionManager.load(getDataFolder());
        // economy
        bankManager = BankManager.getInstance();
        bankManager.load(getDataFolder());
        auctionHouseManager = AuctionHouseManager.getInstance();
        auctionHouseManager.load(getDataFolder());
        bazaarManager = BazaarManager.getInstance();
        bazaarManager.load(getDataFolder());

        // world / progression
        mayorManager = MayorManager.getInstance();
        mayorManager.load(getDataFolder());
        calendarManager = CalendarManager.getInstance();
        essenceManager = EssenceManager.getInstance();
        essenceShopManager = EssenceShopManager.getInstance();
        ShopManager.getInstance().load(getDataFolder());
        dungeonManager = DungeonManager.getInstance();
        dungeonManager.load(getDataFolder());
        dungeonClassManager = DungeonClassManager.getInstance();
        petManager = PetManager.getInstance();
        petManager.load(getDataFolder());
        hotmManager = HOTMManager.getInstance();
        hotmManager.load(getDataFolder());
        slayerManager = SlayerManager.getInstance();
        slayerManager.load(getDataFolder());
        trophyFishManager = TrophyFishManager.getInstance();
        bestiaryManager = BestiaryManager.getInstance();
        bestiaryManager.load(getDataFolder());
        museumManager = MuseumManager.getInstance();
        riftManager = RiftManager.getInstance();
        minionManager = MinionManager.getInstance();
        minionManager.load(getDataFolder());
        minionManager.startTickTask(this);
        forgeManager = ForgeManager.getInstance();
        statManager = StatManager.getInstance();
        wardrobeManager = WardrobeManager.getInstance();
        wardrobeManager.load(getDataFolder());
        accessoryBagManager = AccessoryBagManager.getInstance();
        AccessoryManager.getInstance();
        sackManager = SackManager.getInstance();
        storageManager = StorageManager.getInstance();
        storageCoordinator = com.skyblock.core.manager.StorageManager.getInstance();
        storageCoordinator.loadAll(getDataFolder());
        islandManager = IslandManager.getInstance();
        islandManager.load(getDataFolder());
        islandManager.start(this);
        gardenManager = GardenManager.getInstance();
        gardenManager.load(getDataFolder());
        crimsonIsleManager = CrimsonIsleManager.getInstance();
        dojoManager = DojoManager.getInstance();
        commissionManager = CommissionManager.getInstance();
        chocolateFactoryManager = ChocolateFactoryManager.getInstance();
        enchantingManager = EnchantingManager.getInstance();
        enchantingManager.load(getDataFolder());
        fishingManager = FishingManager.getInstance();
        fishingManager.load(getDataFolder());
        guildManager = GuildManager.getInstance();
        guildManager.load(getDataFolder());
        partyManager = PartyManager.getInstance();
        fairySoulManager = FairySoulManager.getInstance();
        harpManager = HarpManager.getInstance();
        harpManager.load(getDataFolder());
        jerryWorkshopManager = JerryWorkshopManager.getInstance();
        jerryWorkshopManager.load(getDataFolder());
        kuudraManager = KuudraManager.getInstance();
        kuudraManager.load(getDataFolder());
        reforgeManager = ReforgeManager.getInstance();
        reforgeManager.load(getDataFolder());
        craftingManager = CraftingManager.getInstance();
        craftingManager.load(getDataFolder());
        craftingManager.registerRecipes(this);
        questManager = QuestManager.getInstance();
        questManager.load(getDataFolder());
        tradeManager = TradeManager.getInstance();
        backpackManager = BackpackManager.getInstance();
        backpackManager.load(getDataFolder());
        eventManager = EventManager.getInstance();
        foragingManager = ForagingManager.getInstance();
        foragingManager.load(getDataFolder());
        miningManager = MiningManager.getInstance();
        combatManager = CombatManager.getInstance();
        chatManager = ChatManager.getInstance();
        leaderboardManager = LeaderboardManager.getInstance();
        mailManager = MailManager.getInstance();
        mailManager.load(getDataFolder());
        coopManager = CoopManager.getInstance();
        coopManager.load(getDataFolder());
        reputationManager = ReputationManager.getInstance();
        reputationManager.load(getDataFolder());
        vaultManager = VaultManager.getInstance();
        vaultManager.load(getDataFolder());
        friendManager = FriendManager.getInstance();
        friendManager.load(getDataFolder());
        boosterManager = BoosterManager.getInstance();
        boosterManager.load(getDataFolder());
        mailboxManager = MailboxManager.getInstance();
        mailboxManager.load(getDataFolder());
        warpManager = WarpManager.getInstance();
        warpManager.load(getDataFolder());
        achievementManager = AchievementManager.getInstance();
        skyblockLevelManager = SkyblockLevelManager.getInstance();
        titleManager = TitleManager.getInstance();
        titleManager.load(getDataFolder());
        runManager = RunManager.getInstance();
        runManager.load(getDataFolder());
        alchemyManager = AlchemyManager.getInstance();
        alchemyManager.load(getDataFolder());
        crystalHollowsManager = CrystalHollowsManager.getInstance();
        jacobsContestManager = JacobsContestManager.getInstance();
        npcManager = NpcManager.getInstance();
        runecraftingManager = RunecraftingManager.getInstance();
        runecraftingManager.load(getDataFolder());
        manaManager = ManaManager.getInstance();
        skyBlockEventManager = SkyBlockEventManager.getInstance();
        tamingManager = TamingManager.getInstance();
        carpentryManager = CarpentryManager.getInstance();
        carpentryManager.load(getDataFolder());
        mobManager = MobManager.getInstance();
        mobManager.init(this);

        // remaining singleton managers — eager-init so they are ready before first use
        com.skyblock.core.manager.AccessoriesManager.getInstance();
        com.skyblock.core.manager.AttributeManager.getInstance();
        com.skyblock.core.manager.BingoManager.getInstance();
        com.skyblock.core.bossbar.BossBarManager.getInstance();
        com.skyblock.core.collections.manager.CollectionRewardManager.getInstance();
        com.skyblock.core.stats.CombatStatsManager.getInstance();
        com.skyblock.core.mob.CustomMobManager.getInstance();
        com.skyblock.core.manager.DragonManager.getInstance().load(getDataFolder());
        com.skyblock.core.manager.DungeonStatsManager.getInstance().load(getDataFolder());
        com.skyblock.core.farming.manager.FarmingManager.getInstance();
        com.skyblock.core.gemstone.GemstoneManager.getInstance();
        com.skyblock.core.manager.ItemStatManager.getInstance();
        com.skyblock.core.manager.JacobManager.getInstance();
        com.skyblock.core.manager.JacobFarmingManager.getInstance();
        com.skyblock.core.magicfind.MagicFindManager.getInstance();
        com.skyblock.core.manager.NetherwartIslandManager.getInstance();
        com.skyblock.core.manager.NetworthManager.getInstance();
        com.skyblock.core.notification.NotificationManager.getInstance();
        com.skyblock.core.manager.PestManager.getInstance().load(getDataFolder());
        com.skyblock.core.manager.PotionManager.getInstance();
        com.skyblock.core.manager.RuneManager.getInstance();
        com.skyblock.core.enchant.SkyBlockEnchantManager.getInstance();
        com.skyblock.core.manager.EnchantmentManager.getInstance();
        com.skyblock.core.stats.StatsManager.getInstance();
        com.skyblock.core.talisman.manager.TalismanBagManager.getInstance();
        com.skyblock.core.talisman.manager.TalismanManager.getInstance();
        com.skyblock.core.trophy.TrophyManager.getInstance();
        com.skyblock.core.manager.CarnivalManager.getInstance();
        com.skyblock.core.manager.EnderChestManager.getInstance();
        com.skyblock.core.manager.ExperimentationTableManager.getInstance();
    }

    @Override
    public void onEnable() {
        instance = this;
        initManagers();
        BankCommand bankCommand = new BankCommand(bankManager);
        getCommand("bank").setExecutor(bankCommand);
        getCommand("bank").setTabCompleter(bankCommand);
        getCommand("banking").setExecutor(bankCommand);
        getCommand("banking").setTabCompleter(bankCommand);
        MayorCommand mayorCommand = new MayorCommand(mayorManager);
        getCommand("mayor").setExecutor(mayorCommand);
        getCommand("mayor").setTabCompleter(mayorCommand);
        AuctionHouseCommand auctionHouseCommand = new AuctionHouseCommand(auctionHouseManager);
        getCommand("auctionhouse").setExecutor(auctionHouseCommand);
        getCommand("auctionhouse").setTabCompleter(auctionHouseCommand);
        if (getCommand("ah") != null) {
            getCommand("ah").setExecutor(auctionHouseCommand);
            getCommand("ah").setTabCompleter(auctionHouseCommand);
        }
        if (getCommand("auction") != null) {
            getCommand("auction").setExecutor(auctionHouseCommand);
            getCommand("auction").setTabCompleter(auctionHouseCommand);
        }
        BazaarCommand bazaarCommand = new BazaarCommand(bazaarManager);
        if (getCommand("bazaar") != null) {
            getCommand("bazaar").setExecutor(bazaarCommand);
            getCommand("bazaar").setTabCompleter(bazaarCommand);
        }
        if (getCommand("bz") != null) {
            getCommand("bz").setExecutor(bazaarCommand);
            getCommand("bz").setTabCompleter(bazaarCommand);
        }
        MuseumCommand museumCommand = new MuseumCommand(museumManager);
        if (getCommand("museum") != null) {
            getCommand("museum").setExecutor(museumCommand);
            getCommand("museum").setTabCompleter(museumCommand);
        }
        EssenceCommand essenceCommand = new EssenceCommand(essenceManager);
        if (getCommand("essence") != null) {
            getCommand("essence").setExecutor(essenceCommand);
            getCommand("essence").setTabCompleter(essenceCommand);
        }
        MenuCommand essenceShopCommand = new MenuCommand(p -> new EssenceShopMenu(p).open(p));
        if (getCommand("essenceshop") != null) {
            getCommand("essenceshop").setExecutor(essenceShopCommand);
            getCommand("essenceshop").setTabCompleter(essenceShopCommand);
        }
        ShopCommand shopCommand = new ShopCommand(ShopManager.getInstance());
        if (getCommand("shop") != null) {
            getCommand("shop").setExecutor(shopCommand);
            getCommand("shop").setTabCompleter(shopCommand);
        }
        DungeonCommand dungeonCommand = new DungeonCommand(dungeonManager);
        getCommand("dungeon").setExecutor(dungeonCommand);
        getCommand("dungeon").setTabCompleter(dungeonCommand);
        DungeonClassCommand dungeonClassCommand = new DungeonClassCommand(dungeonClassManager);
        if (getCommand("dungeonclass") != null) {
            getCommand("dungeonclass").setExecutor(dungeonClassCommand);
            getCommand("dungeonclass").setTabCompleter(dungeonClassCommand);
        }
        GuildCommand guildCommand = new GuildCommand(guildManager);
        getCommand("guild").setExecutor(guildCommand);
        getCommand("guild").setTabCompleter(guildCommand);
        PartyCommand partyCommand = new PartyCommand(partyManager);
        getCommand("party").setExecutor(partyCommand);
        getCommand("party").setTabCompleter(partyCommand);
        PetCommand petCommand = new PetCommand(petManager);
        getCommand("pet").setExecutor(petCommand);
        getCommand("pet").setTabCompleter(petCommand);
        AbilityCommand abilityCommand = new AbilityCommand(ItemAbilityManager.getInstance());
        if (getCommand("ability") != null) {
            getCommand("ability").setExecutor(abilityCommand);
            getCommand("ability").setTabCompleter(abilityCommand);
        }
        MenuCommand petsCommand = new MenuCommand(p -> new PetMenu(this, p).open(p));
        if (getCommand("pets") != null) {
            getCommand("pets").setExecutor(petsCommand);
        }
        MenuCommand gardenCommand = new MenuCommand(p -> new GardenMenu(p).open(p));
        getCommand("garden").setExecutor(gardenCommand);
        MenuCommand dungeonsCommand = new MenuCommand(p -> new DungeonsMenu(p.getUniqueId()).open(p));
        if (getCommand("dungeons") != null) {
            getCommand("dungeons").setExecutor(dungeonsCommand);
        }
        MenuCommand collectionsMenuCommand = new MenuCommand(p -> new CollectionsMenu(p.getUniqueId()).open(p));
        if (getCommand("collectionsmenu") != null) {
            getCommand("collectionsmenu").setExecutor(collectionsMenuCommand);
        }
        MenuCommand minionMenuCommand = new MenuCommand(p -> new MinionMenu(this, p).open(p));
        if (getCommand("minionsmenu") != null) {
            getCommand("minionsmenu").setExecutor(minionMenuCommand);
        }
        SlayerCommand slayerCommand = new SlayerCommand(this, slayerManager);
        getCommand("slay").setExecutor(slayerCommand);
        getCommand("slay").setTabCompleter(slayerCommand);
        if (getCommand("slayer") != null) {
            getCommand("slayer").setExecutor(slayerCommand);
            getCommand("slayer").setTabCompleter(slayerCommand);
        }
        getServer().getPluginManager().registerEvents(SlayerListener.getInstance(), this);
        FishingCommand fishingCommand = new FishingCommand(fishingManager, trophyFishManager);
        getCommand("fishing").setExecutor(fishingCommand);
        getCommand("fishing").setTabCompleter(fishingCommand);
        getServer().getPluginManager().registerEvents(new TrophyFishListener(trophyFishManager, fishingManager), this);
        FairySoulCommand fairySoulCommand = new FairySoulCommand(fairySoulManager);
        if (getCommand("fairysoul") != null) {
            getCommand("fairysoul").setExecutor(fairySoulCommand);
            getCommand("fairysoul").setTabCompleter(fairySoulCommand);
        }
        BestiaryCommand bestiaryCommand = new BestiaryCommand(bestiaryManager);
        if (getCommand("bestiary") != null) {
            getCommand("bestiary").setExecutor(bestiaryCommand);
            getCommand("bestiary").setTabCompleter(bestiaryCommand);
        }
        TrophyFishCommand trophyFishCommand = new TrophyFishCommand(trophyFishManager);
        if (getCommand("trophyfish") != null) {
            getCommand("trophyfish").setExecutor(trophyFishCommand);
            getCommand("trophyfish").setTabCompleter(trophyFishCommand);
        }
        MenuCommand trophyFishingCommand = new MenuCommand(p -> new TrophyFishingMenu(p).open(p));
        if (getCommand("trophyfishing") != null) {
            getCommand("trophyfishing").setExecutor(trophyFishingCommand);
            getCommand("trophyfishing").setTabCompleter(trophyFishingCommand);
        }
        HarpCommand harpCommand = new HarpCommand(harpManager);
        if (getCommand("harp") != null) {
            getCommand("harp").setExecutor(harpCommand);
            getCommand("harp").setTabCompleter(harpCommand);
        }
        JerryWorkshopCommand jerryWorkshopCommand = new JerryWorkshopCommand(jerryWorkshopManager);
        if (getCommand("jerryworkshop") != null) {
            getCommand("jerryworkshop").setExecutor(jerryWorkshopCommand);
            getCommand("jerryworkshop").setTabCompleter(jerryWorkshopCommand);
        }
        HOTMCommand hotmCommand = new HOTMCommand(hotmManager);
        getCommand("hotmtree").setExecutor(hotmCommand);
        getCommand("hotmtree").setTabCompleter(hotmCommand);
        if (getCommand("hotm") != null) {
            getCommand("hotm").setExecutor(hotmCommand);
            getCommand("hotm").setTabCompleter(hotmCommand);
        }
        KuudraCommand kuudraCommand = new KuudraCommand(kuudraManager);
        getCommand("kuudra").setExecutor(kuudraCommand);
        getCommand("kuudra").setTabCompleter(kuudraCommand);
        EnchantingCommand enchantingCommand = new EnchantingCommand(enchantingManager);
        getCommand("enchanting").setExecutor(enchantingCommand);
        getCommand("enchanting").setTabCompleter(enchantingCommand);
        ReforgeCommand reforgeCommand = new ReforgeCommand(reforgeManager);
        getCommand("reforge").setExecutor(reforgeCommand);
        getCommand("reforge").setTabCompleter(reforgeCommand);
        SkillsCommand skillsCommand = new SkillsCommand(skillsManager);
        getCommand("skills").setExecutor(skillsCommand);
        getCommand("skills").setTabCompleter(skillsCommand);
        ProfileCommand profileCommand = new ProfileCommand();
        getCommand("profile").setExecutor(profileCommand);
        getCommand("profile").setTabCompleter(profileCommand);
        IslandCommand islandCommand = new IslandCommand();
        if (getCommand("island") != null) {
            getCommand("island").setExecutor(islandCommand);
        }
        MinionCommand minionCommand = new MinionCommand(minionManager);
        getCommand("minion").setExecutor(minionCommand);
        getCommand("minion").setTabCompleter(minionCommand);
        if (getCommand("minions") != null) {
            getCommand("minions").setExecutor(minionCommand);
            getCommand("minions").setTabCompleter(minionCommand);
        }
        CraftingCommand craftingCommand = new CraftingCommand(craftingManager);
        getCommand("crafting").setExecutor(craftingCommand);
        getCommand("crafting").setTabCompleter(craftingCommand);
        QuestCommand questCommand = new QuestCommand(questManager);
        getCommand("quest").setExecutor(questCommand);
        getCommand("quest").setTabCompleter(questCommand);
        TradeCommand tradeCommand = new TradeCommand(tradeManager);
        getCommand("trade").setExecutor(tradeCommand);
        getCommand("trade").setTabCompleter(tradeCommand);
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        BackpackCommand backpackCommand = new BackpackCommand(backpackManager);
        getCommand("backpack").setExecutor(backpackCommand);
        getCommand("backpack").setTabCompleter(backpackCommand);
        EventCommand eventCommand = new EventCommand(eventManager);
        getCommand("event").setExecutor(eventCommand);
        getCommand("event").setTabCompleter(eventCommand);
        ForagingCommand foragingCommand = new ForagingCommand(foragingManager);
        getCommand("foraging").setExecutor(foragingCommand);
        getCommand("foraging").setTabCompleter(foragingCommand);
        MiningCommand miningCommand = new MiningCommand(miningManager);
        if (getCommand("mining") != null) {
            getCommand("mining").setExecutor(miningCommand);
            getCommand("mining").setTabCompleter(miningCommand);
        }
        if (getCommand("miningzone") != null) {
            MiningZoneCommand miningZoneCommand =
                    new MiningZoneCommand(MiningZoneManager.getInstance(), miningManager);
            getCommand("miningzone").setExecutor(miningZoneCommand);
            getCommand("miningzone").setTabCompleter(miningZoneCommand);
        }
        CombatCommand combatCommand = new CombatCommand(combatManager);
        getCommand("combat").setExecutor(combatCommand);
        getCommand("combat").setTabCompleter(combatCommand);
        CollectionsCommand collectionsCommand = new CollectionsCommand(collectionManager);
        getCommand("collections").setExecutor(collectionsCommand);
        getCommand("collections").setTabCompleter(collectionsCommand);
        if (getCommand("collection") != null) {
            CollectionCommand collectionCommand = new CollectionCommand();
            getCommand("collection").setExecutor(collectionCommand);
            getCommand("collection").setTabCompleter(collectionCommand);
        }
        getServer().getPluginManager().registerEvents(new CollectionListener(collectionManager), this);
        getServer().getPluginManager().registerEvents(new ProgressionListener(skillsManager), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.listener.FairySoulListener(FairySoulManager.getInstance(), this), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.BestiaryListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.FishingListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.SkillListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.ChatListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.PlayerEventListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.CombatListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.AbilityListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.MinionListener.getInstance(), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.mob.MobLootListener(com.skyblock.core.mob.MobLootManager.getInstance()), this);
        ChatCommand chatCommand = new ChatCommand(chatManager);
        getCommand("chat").setExecutor(chatCommand);
        getCommand("chat").setTabCompleter(chatCommand);
        LeaderboardCommand leaderboardCommand = new LeaderboardCommand(leaderboardManager);
        getCommand("leaderboard").setExecutor(leaderboardCommand);
        getCommand("leaderboard").setTabCompleter(leaderboardCommand);
        MailCommand mailCommand = new MailCommand(mailManager);
        if (getCommand("mail") != null) {
            getCommand("mail").setExecutor(mailCommand);
            getCommand("mail").setTabCompleter(mailCommand);
        }
        com.skyblock.core.command.ForgeCommand forgeCommand = new com.skyblock.core.command.ForgeCommand(forgeManager);
        getCommand("forge").setExecutor(forgeCommand);
        getCommand("forge").setTabCompleter(forgeCommand);
        EnchantCommand enchantCommand = new EnchantCommand();
        getCommand("enchant").setExecutor(enchantCommand);
        getCommand("enchant").setTabCompleter(enchantCommand);
        MenuCommand talismanCommand = new MenuCommand(p -> new com.skyblock.core.menu.TalismanBagMenu(this, p).open(p));
        if (getCommand("talisman") != null) {
            getCommand("talisman").setExecutor(talismanCommand);
            getCommand("talisman").setTabCompleter(talismanCommand);
        }
        EnderChestCommand enderChestCommand = new EnderChestCommand();
        if (getCommand("enderchest") != null) {
            getCommand("enderchest").setExecutor(enderChestCommand);
            getCommand("enderchest").setTabCompleter(enderChestCommand);
        }
        com.skyblock.core.command.RuneCommand runeCommand = new com.skyblock.core.command.RuneCommand(this);
        if (getCommand("runes") != null) {
            getCommand("runes").setExecutor(runeCommand);
        }
        com.skyblock.core.command.RunecraftingCommand runecraftingCommand = new com.skyblock.core.command.RunecraftingCommand();
        if (getCommand("runecrafting") != null) {
            getCommand("runecrafting").setExecutor(runecraftingCommand);
        }
        CalendarCommand calendarCommand = new CalendarCommand();
        if (getCommand("calendar") != null) {
            getCommand("calendar").setExecutor(calendarCommand);
            getCommand("calendar").setTabCompleter(calendarCommand);
        }
        CoopCommand coopCommand = new CoopCommand(coopManager);
        getCommand("coop").setExecutor(coopCommand);
        getCommand("coop").setTabCompleter(coopCommand);
        CrimsonCommand crimsonCommand = new CrimsonCommand(reputationManager);
        getCommand("crimson").setExecutor(crimsonCommand);
        getCommand("crimson").setTabCompleter(crimsonCommand);
        MenuCommand crimsonIsleCommand = new MenuCommand(p -> new CrimsonIsleMenu(p.getUniqueId()).open(p));
        if (getCommand("crimsonisle") != null) {
            getCommand("crimsonisle").setExecutor(crimsonIsleCommand);
            getCommand("crimsonisle").setTabCompleter(crimsonIsleCommand);
        }
        MenuCommand dojoCommand = new MenuCommand(p -> new DojoMenu(p.getUniqueId()).open(p));
        if (getCommand("dojo") != null) {
            getCommand("dojo").setExecutor(dojoCommand);
            getCommand("dojo").setTabCompleter(dojoCommand);
        }
        MenuCommand miningCommissionCommand = new MenuCommand(p -> new MiningCommissionMenu(p).open(p));
        if (getCommand("miningcommission") != null) {
            getCommand("miningcommission").setExecutor(miningCommissionCommand);
            getCommand("miningcommission").setTabCompleter(miningCommissionCommand);
        }
        MenuCommand chocolateFactoryCommand = new MenuCommand(p -> new ChocolateFactoryMenu(p).open(p));
        if (getCommand("chocolatefactory") != null) {
            getCommand("chocolatefactory").setExecutor(chocolateFactoryCommand);
            getCommand("chocolatefactory").setTabCompleter(chocolateFactoryCommand);
        }
        MenuCommand bazaarMenuCommand = new MenuCommand(p -> new BazaarMenu(this, p).open(p));
        if (getCommand("bazaarmenu") != null) {
            getCommand("bazaarmenu").setExecutor(bazaarMenuCommand);
            getCommand("bazaarmenu").setTabCompleter(bazaarMenuCommand);
        }
        MenuCommand foragingMenuCommand = new MenuCommand(p -> new ForagingMenu(p.getUniqueId()).open(p));
        if (getCommand("foragingmenu") != null) {
            getCommand("foragingmenu").setExecutor(foragingMenuCommand);
            getCommand("foragingmenu").setTabCompleter(foragingMenuCommand);
        }
        MenuCommand jacobsContestCommand = new MenuCommand(p -> new JacobsContestMenu(p.getUniqueId()).open(p));
        if (getCommand("jacobscontest") != null) {
            getCommand("jacobscontest").setExecutor(jacobsContestCommand);
            getCommand("jacobscontest").setTabCompleter(jacobsContestCommand);
        }
        VaultCommand vaultCommand = new VaultCommand(vaultManager);
        getCommand("vault").setExecutor(vaultCommand);
        getCommand("vault").setTabCompleter(vaultCommand);
        FriendCommand friendCommand = new FriendCommand(friendManager);
        getCommand("friend").setExecutor(friendCommand);
        getCommand("friend").setTabCompleter(friendCommand);
        BoosterCommand boosterCommand = new BoosterCommand(boosterManager);
        getCommand("booster").setExecutor(boosterCommand);
        getCommand("booster").setTabCompleter(boosterCommand);
        MailboxCommand mailboxCommand = new MailboxCommand(mailboxManager);
        getCommand("mailbox").setExecutor(mailboxCommand);
        getCommand("mailbox").setTabCompleter(mailboxCommand);
        WarpCommand warpCommand = new WarpCommand(warpManager);
        getCommand("warp").setExecutor(warpCommand);
        getCommand("warp").setTabCompleter(warpCommand);
        getCommand("hub").setExecutor(warpCommand);
        AchievementCommand achievementCommand = new AchievementCommand(achievementManager);
        getCommand("achievement").setExecutor(achievementCommand);
        getCommand("achievement").setTabCompleter(achievementCommand);
        StatCommand statCommand = new StatCommand(statManager);
        getCommand("stat").setExecutor(statCommand);
        getCommand("stat").setTabCompleter(statCommand);
        MenuCommand statsCommand = new MenuCommand(p -> new StatsMenu(p).open(p));
        if (getCommand("stats") != null) {
            getCommand("stats").setExecutor(statsCommand);
            getCommand("stats").setTabCompleter(statsCommand);
        }
        SkyblockLevelCommand skyblockLevelCommand = new SkyblockLevelCommand(skyblockLevelManager);
        getCommand("skyblock-level").setExecutor(skyblockLevelCommand);
        getCommand("skyblock-level").setTabCompleter(skyblockLevelCommand);
        MenuCommand menuCommand = new MenuCommand(p -> SkyBlockMenuManager.getInstance().openMainMenu(p));
        if (getCommand("menu") != null) {
            getCommand("menu").setExecutor(menuCommand);
            getCommand("menu").setTabCompleter(menuCommand);
        }
        SkyBlockMenuCommand skyBlockCommand = new SkyBlockMenuCommand();
        if (getCommand("skyblock") != null) {
            getCommand("skyblock").setExecutor(skyBlockCommand);
            getCommand("skyblock").setTabCompleter(skyBlockCommand);
        }
        TitleCommand titleCommand = new TitleCommand(titleManager);
        if (getCommand("title") != null) {
            getCommand("title").setExecutor(titleCommand);
            getCommand("title").setTabCompleter(titleCommand);
        }
        RunCommand runCommand = new RunCommand(runManager);
        if (getCommand("run") != null) {
            getCommand("run").setExecutor(runCommand);
            getCommand("run").setTabCompleter(runCommand);
        }
        AlchemyCommand alchemyCommand = new AlchemyCommand(alchemyManager);
        getCommand("alchemy").setExecutor(alchemyCommand);
        getCommand("alchemy").setTabCompleter(alchemyCommand);
        WardrobeCommand wardrobeCommand = new WardrobeCommand(wardrobeManager);
        getCommand("wardrobe").setExecutor(wardrobeCommand);
        getCommand("wardrobe").setTabCompleter(wardrobeCommand);
        AccessoryBagCommand accessoryBagCommand = new AccessoryBagCommand(accessoryBagManager);
        getCommand("accessories").setExecutor(accessoryBagCommand);
        getCommand("accessories").setTabCompleter(accessoryBagCommand);
        if (getCommand("accessorybag") != null) {
            getCommand("accessorybag").setExecutor(accessoryBagCommand);
            getCommand("accessorybag").setTabCompleter(accessoryBagCommand);
        }
        RiftCommand riftCommand = new RiftCommand(riftManager);
        getCommand("rift").setExecutor(riftCommand);
        getCommand("rift").setTabCompleter(riftCommand);
        getServer().getPluginManager().registerEvents(riftManager, this);
        SackCommand sackCommand = new SackCommand(sackManager);
        if (getCommand("sack") != null) {
            getCommand("sack").setExecutor(sackCommand);
            getCommand("sack").setTabCompleter(sackCommand);
        }
        CrystalHollowsCommand crystalHollowsCommand = new CrystalHollowsCommand(crystalHollowsManager);
        getCommand("crystalhollows").setExecutor(crystalHollowsCommand);
        getCommand("crystalhollows").setTabCompleter(crystalHollowsCommand);
        StorageCommand storageCommand = new StorageCommand(storageCoordinator);
        if (getCommand("storage") != null) {
            getCommand("storage").setExecutor(storageCommand);
            getCommand("storage").setTabCompleter(storageCommand);
        }
        ScoreboardManager.getInstance().start(this);
        getServer().getPluginManager().registerEvents(com.skyblock.core.listener.ScoreboardListener.getInstance(), this);
        manaManager.start(this);
        skyBlockEventManager.start(this);
        jacobsContestManager.start(this);
        NpcCommand npcCommand = new NpcCommand(npcManager, com.skyblock.core.manager.EconomyManager.getInstance());
        if (getCommand("npc") != null) {
            getCommand("npc").setExecutor(npcCommand);
            getCommand("npc").setTabCompleter(npcCommand);
        }
        getServer().getPluginManager().registerEvents(new NPCListener(this, npcManager), this);
        getServer().getPluginManager().registerEvents(PlayerDataManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(tamingManager, this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.armor.ArmorSetListener(com.skyblock.core.armor.ArmorSetManager.getInstance()), this);
        ActionBarManager.getInstance().start(this);
        DailyRewardCommand dailyRewardCommand = new DailyRewardCommand(DailyRewardManager.getInstance(), com.skyblock.core.manager.EconomyManager.getInstance());
        if (getCommand("dailyreward") != null) {
            getCommand("dailyreward").setExecutor(dailyRewardCommand);
            getCommand("dailyreward").setTabCompleter(dailyRewardCommand);
        }
        com.skyblock.core.command.SeasonCommand seasonCommand = new com.skyblock.core.command.SeasonCommand(com.skyblock.core.season.SeasonManager.getInstance());
        if (getCommand("season") != null) {
            getCommand("season").setExecutor(seasonCommand);
            getCommand("season").setTabCompleter(seasonCommand);
        }
        RepairCommand repairCommand = new RepairCommand(RepairManager.getInstance());
        if (getCommand("repair") != null) {
            getCommand("repair").setExecutor(repairCommand);
            getCommand("repair").setTabCompleter(repairCommand);
        }
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        DungeonManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        BestiaryManager.getInstance().save(getDataFolder());
        fishingManager.save(getDataFolder());
        IslandManager.getInstance().save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        HOTMManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        ReforgeManager.getInstance().save(getDataFolder());
        MinionManager.getInstance().stopTickTask();
        MinionManager.getInstance().save(getDataFolder());
        SkillManager.getInstance().save(getDataFolder());
        ProfileManager.getInstance().save(getDataFolder());
        CraftingManager.getInstance().save(getDataFolder());
        QuestManager.getInstance().save(getDataFolder());
        BackpackManager.getInstance().save(getDataFolder());
        ForagingManager.getInstance().save(getDataFolder());
        CollectionManager.getInstance().save(getDataFolder());
        MailManager.getInstance().save(getDataFolder());
        CoopManager.getInstance().save(getDataFolder());
        ReputationManager.getInstance().save(getDataFolder());
        VaultManager.getInstance().save(getDataFolder());
        GuildManager.getInstance().save(getDataFolder());
        FriendManager.getInstance().save(getDataFolder());
        BoosterManager.getInstance().save(getDataFolder());
        MailboxManager.getInstance().save(getDataFolder());
        TitleManager.getInstance().save(getDataFolder());
        RunManager.getInstance().save(getDataFolder());
        AlchemyManager.getInstance().save(getDataFolder());
        JerryWorkshopManager.getInstance().save(getDataFolder());
        PetManager.getInstance().save(getDataFolder());
        WardrobeManager.getInstance().save(getDataFolder());
        storageCoordinator.saveAll(getDataFolder());
        try {
            WarpManager.getInstance().save(getDataFolder());
            HarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save data: " + e.getMessage());
        }
        RunecraftingManager.getInstance().save(getDataFolder());
        CarpentryManager.getInstance().save(getDataFolder());
        SkyBlockEventManager.getInstance().stop();
        ScoreboardManager.getInstance().stop();
        instance = null;
    }
}
