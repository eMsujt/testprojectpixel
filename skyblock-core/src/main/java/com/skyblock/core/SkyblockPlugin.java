package com.skyblock.core;

import com.skyblock.core.manager.AccessoryManager;
import com.skyblock.core.quest.command.QuestCommand;
import com.skyblock.core.crafting.CraftingCommand;
import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.manager.QuestManager;
import com.skyblock.core.auction.command.AuctionHouseCommand;
import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.bank.command.BankCommand;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.bazaar.command.BazaarCommand;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.CalendarCommand;
import com.skyblock.core.manager.CalendarManager;
import com.skyblock.core.museum.MuseumCommand;
import com.skyblock.core.manager.MuseumManager;
import com.skyblock.core.manager.EssenceCommand;
import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.dungeon.command.DungeonCommand;
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
import com.skyblock.core.manager.BestiaryCommand;
import com.skyblock.core.manager.HarpCommand;
import com.skyblock.core.manager.TrophyFishCommand;
import com.skyblock.core.command.MenuCommand;
import com.skyblock.core.menu.PetMenu;
import com.skyblock.core.menu.StatsMenu;
import com.skyblock.core.menu.TrophyFishingMenu;
import com.skyblock.core.manager.JerryWorkshopCommand;
import com.skyblock.core.manager.BestiaryManager;
import com.skyblock.core.manager.HarpManager;
import com.skyblock.core.manager.JerryWorkshopManager;
import com.skyblock.core.garden.GardenCommand;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.guild.GuildCommand;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.hotm.command.HOTMCommand;
import com.skyblock.core.manager.HotmManager;
import com.skyblock.core.manager.KuudraManager;
import com.skyblock.core.mayor.MayorCommand;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.party.PartyCommand;
import com.skyblock.core.manager.PartyManager;
import com.skyblock.core.pet.PetCommand;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.PetsManager;
import com.skyblock.core.command.ProfileCommand;
import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.reforge.ReforgeCommand;
import com.skyblock.core.backpack.BackpackCommand;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.event.EventCommand;
import com.skyblock.core.manager.EventManager;
import com.skyblock.core.chat.ChatCommand;
import com.skyblock.core.chat.ChatManager;
import com.skyblock.core.leaderboard.LeaderboardCommand;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.mail.MailCommand;
import com.skyblock.core.mail.MailManager;
import com.skyblock.core.collections.command.CollectionsCommand;
import com.skyblock.core.collections.listener.CollectionListener;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.command.EssenceShopCommand;
import com.skyblock.core.combat.command.CombatCommand;
import com.skyblock.core.combat.manager.CombatManager;
import com.skyblock.core.coop.CoopCommand;
import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.crimson.CrimsonCommand;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.CrimsonIsleManager;
import com.skyblock.core.vault.VaultCommand;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.booster.BoosterCommand;
import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.mailbox.MailboxCommand;
import com.skyblock.core.mailbox.MailboxManager;
import com.skyblock.core.friend.FriendCommand;
import com.skyblock.core.friend.FriendManager;
import com.skyblock.core.forge.ForgeCommand;
import com.skyblock.core.manager.ForgeManager;
import com.skyblock.core.foraging.ForagingCommand;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.manager.TradeManager;
import com.skyblock.core.trade.TradeCommand;
import com.skyblock.core.trade.TradeListener;
import com.skyblock.core.achievement.AchievementCommand;
import com.skyblock.core.achievement.AchievementManager;
import com.skyblock.core.level.SkyblockLevelCommand;
import com.skyblock.core.manager.SkyblockLevelManager;
import com.skyblock.core.command.SkyblockMenuCommand;
import com.skyblock.core.run.RunCommand;
import com.skyblock.core.dungeon.manager.RunManager;
import com.skyblock.core.title.TitleCommand;
import com.skyblock.core.title.TitleManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.skills.command.SkillsCommand;
import com.skyblock.core.slayer.command.SlayerCommand;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.StatCommand;
import com.skyblock.core.manager.StatManager;
import com.skyblock.core.command.SackCommand;
import com.skyblock.core.manager.SackManager;
import com.skyblock.core.warp.WarpCommand;
import com.skyblock.core.warp.WarpManager;
import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.manager.WardrobeManager;
import com.skyblock.core.accessory.command.AccessoryBagCommand;
import com.skyblock.core.manager.AccessoryBagManager;
import com.skyblock.core.rift.RiftCommand;
import com.skyblock.core.rift.RiftListener;
import com.skyblock.core.manager.RiftManager;
import com.skyblock.core.crystalhollows.CrystalHollowsCommand;
import com.skyblock.core.manager.CrystalHollowsManager;
import com.skyblock.core.manager.BankingManager;
import com.skyblock.core.storage.StorageManager;
import com.skyblock.core.storage.StorageCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyblockPlugin extends JavaPlugin {

    private static SkyblockPlugin instance;
    private BankManager bankManager;
    private BestiaryManager bestiaryManager;
    private MuseumManager museumManager;
    private RiftManager riftManager;
    private MayorManager mayorManager;
    private StatManager statManager;
    private WardrobeManager wardrobeManager;
    private AccessoryBagManager accessoryBagManager;
    private TrophyFishManager trophyFishManager;
    private DungeonClassManager dungeonClassManager;
    private ForgeManager forgeManager;
    private AuctionHouseManager auctionHouseManager;
    private PetManager petManager;
    private PetsManager petsManager;
    private HotmManager hotmManager;
    private SkillManager skillsManager;
    private MinionManager minionManager;
    private SlayerManager slayerManager;
    private BazaarManager bazaarManager;
    private CalendarManager calendarManager;
    private DungeonManager dungeonManager;
    private FishingManager fishingManager;
    private EssenceManager essenceManager;
    private EssenceShopManager essenceShopManager;
    private CollectionManager collectionManager;
    private SackManager sackManager;
    private BankingManager bankingManager;
    private StorageManager storageManager;
    private ProfileManager profile;

    public static SkyblockPlugin getInstance() {
        return instance;
    }

    private void initManagers() {
        bankManager = BankManager.getInstance();
        bankManager.load(getDataFolder());
        bankingManager = BankingManager.getInstance();
        mayorManager = MayorManager.getInstance();
        mayorManager.load(getDataFolder());
        auctionHouseManager = AuctionHouseManager.getInstance();
        auctionHouseManager.load(getDataFolder());
        bazaarManager = BazaarManager.getInstance();
        bazaarManager.load(getDataFolder());
        calendarManager = CalendarManager.getInstance();
        essenceManager = EssenceManager.getInstance();
        essenceShopManager = EssenceShopManager.getInstance();
        collectionManager = CollectionManager.getInstance();
        collectionManager.load(getDataFolder());
        dungeonManager = DungeonManager.getInstance();
        dungeonManager.load(getDataFolder());
        dungeonClassManager = DungeonClassManager.getInstance();
        petManager = PetManager.getInstance();
        petManager.load(getDataFolder());
        petsManager = PetsManager.getInstance();
        hotmManager = HotmManager.getInstance();
        hotmManager.load(getDataFolder());
        slayerManager = SlayerManager.getInstance();
        slayerManager.load(getDataFolder());
        trophyFishManager = TrophyFishManager.getInstance();
        bestiaryManager = BestiaryManager.getInstance();
        museumManager = MuseumManager.getInstance();
        riftManager = RiftManager.getInstance();
        skillsManager = SkillManager.getInstance();
        skillsManager.load(getDataFolder());
        profile = ProfileManager.getInstance();
        profile.load(getDataFolder());
        minionManager = MinionManager.getInstance();
        minionManager.load(getDataFolder());
        forgeManager = ForgeManager.getInstance();
        statManager = StatManager.getInstance();
        wardrobeManager = WardrobeManager.getInstance();
        wardrobeManager.load(getDataFolder());
        accessoryBagManager = AccessoryBagManager.getInstance();
        sackManager = SackManager.getInstance();
        storageManager = StorageManager.getInstance();
    }

    @Override
    public void onEnable() {
        instance = this;
        initManagers();
        BankCommand bankCommand = new BankCommand(bankManager);
        getCommand("bank").setExecutor(bankCommand);
        getCommand("bank").setTabCompleter(bankCommand);
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
        EssenceShopCommand essenceShopCommand = new EssenceShopCommand(essenceShopManager);
        if (getCommand("essenceshop") != null) {
            getCommand("essenceshop").setExecutor(essenceShopCommand);
            getCommand("essenceshop").setTabCompleter(essenceShopCommand);
        }
        DungeonCommand dungeonCommand = new DungeonCommand(dungeonManager);
        getCommand("dungeon").setExecutor(dungeonCommand);
        getCommand("dungeon").setTabCompleter(dungeonCommand);
        DungeonClassCommand dungeonClassCommand = new DungeonClassCommand(dungeonClassManager);
        if (getCommand("dungeonclass") != null) {
            getCommand("dungeonclass").setExecutor(dungeonClassCommand);
            getCommand("dungeonclass").setTabCompleter(dungeonClassCommand);
        }
        GuildManager guildManager = GuildManager.getInstance();
        guildManager.load(getDataFolder());
        GuildCommand guildCommand = new GuildCommand(guildManager);
        getCommand("guild").setExecutor(guildCommand);
        getCommand("guild").setTabCompleter(guildCommand);
        PartyManager partyManager = PartyManager.getInstance();
        PartyCommand partyCommand = new PartyCommand(partyManager);
        getCommand("party").setExecutor(partyCommand);
        getCommand("party").setTabCompleter(partyCommand);
        PetCommand petCommand = new PetCommand(petManager);
        getCommand("pet").setExecutor(petCommand);
        getCommand("pet").setTabCompleter(petCommand);
        MenuCommand petsCommand = new MenuCommand(p -> new PetMenu(p).open(p));
        if (getCommand("pets") != null) {
            getCommand("pets").setExecutor(petsCommand);
        }
        GardenManager gardenManager = GardenManager.getInstance();
        gardenManager.load(getDataFolder());
        GardenCommand gardenCommand = new GardenCommand(gardenManager);
        getCommand("garden").setExecutor(gardenCommand);
        getCommand("garden").setTabCompleter(gardenCommand);
        SlayerCommand slayerCommand = new SlayerCommand(slayerManager);
        getCommand("slay").setExecutor(slayerCommand);
        getCommand("slay").setTabCompleter(slayerCommand);
        getCommand("slayer").setExecutor(slayerCommand);
        getCommand("slayer").setTabCompleter(slayerCommand);
        fishingManager = FishingManager.getInstance();
        fishingManager.load(getDataFolder());
        FishingCommand fishingCommand = new FishingCommand(fishingManager, trophyFishManager);
        getCommand("fishing").setExecutor(fishingCommand);
        getCommand("fishing").setTabCompleter(fishingCommand);
        getServer().getPluginManager().registerEvents(new TrophyFishListener(trophyFishManager, fishingManager), this);
        FairySoulManager fairySoulManager = FairySoulManager.getInstance();
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
        HarpManager harpManager = HarpManager.getInstance();
        harpManager.load(getDataFolder());
        HarpCommand harpCommand = new HarpCommand(harpManager);
        if (getCommand("harp") != null) {
            getCommand("harp").setExecutor(harpCommand);
            getCommand("harp").setTabCompleter(harpCommand);
        }
        JerryWorkshopManager jerryWorkshopManager = JerryWorkshopManager.getInstance();
        jerryWorkshopManager.load(getDataFolder());
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
        KuudraManager kuudraManager = KuudraManager.getInstance();
        kuudraManager.load(getDataFolder());
        KuudraCommand kuudraCommand = new KuudraCommand(kuudraManager);
        getCommand("kuudra").setExecutor(kuudraCommand);
        getCommand("kuudra").setTabCompleter(kuudraCommand);
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        enchantingManager.load(getDataFolder());
        EnchantingCommand enchantingCommand = new EnchantingCommand(enchantingManager);
        getCommand("enchanting").setExecutor(enchantingCommand);
        getCommand("enchanting").setTabCompleter(enchantingCommand);
        ReforgeManager reforgeManager = ReforgeManager.getInstance();
        reforgeManager.load(getDataFolder());
        ReforgeCommand reforgeCommand = new ReforgeCommand(reforgeManager);
        getCommand("reforge").setExecutor(reforgeCommand);
        getCommand("reforge").setTabCompleter(reforgeCommand);
        AccessoryManager.getInstance();
        SkillsCommand skillsCommand = new SkillsCommand(skillsManager);
        getCommand("skills").setExecutor(skillsCommand);
        getCommand("skills").setTabCompleter(skillsCommand);
        ProfileCommand profileCommand = new ProfileCommand(profile);
        getCommand("profile").setExecutor(profileCommand);
        getCommand("profile").setTabCompleter(profileCommand);
        MinionCommand minionCommand = new MinionCommand(minionManager);
        getCommand("minion").setExecutor(minionCommand);
        getCommand("minion").setTabCompleter(minionCommand);
        if (getCommand("minions") != null) {
            getCommand("minions").setExecutor(minionCommand);
            getCommand("minions").setTabCompleter(minionCommand);
        }
        CraftingManager craftingManager = CraftingManager.getInstance();
        craftingManager.load(getDataFolder());
        CraftingCommand craftingCommand = new CraftingCommand(craftingManager);
        getCommand("crafting").setExecutor(craftingCommand);
        getCommand("crafting").setTabCompleter(craftingCommand);
        QuestManager questManager = QuestManager.getInstance();
        questManager.load(getDataFolder());
        QuestCommand questCommand = new QuestCommand(questManager);
        getCommand("quest").setExecutor(questCommand);
        getCommand("quest").setTabCompleter(questCommand);
        TradeManager tradeManager = TradeManager.getInstance();
        TradeCommand tradeCommand = new TradeCommand(tradeManager);
        getCommand("trade").setExecutor(tradeCommand);
        getCommand("trade").setTabCompleter(tradeCommand);
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager), this);
        BackpackManager backpackManager = BackpackManager.getInstance();
        backpackManager.load(getDataFolder());
        BackpackCommand backpackCommand = new BackpackCommand(backpackManager);
        getCommand("backpack").setExecutor(backpackCommand);
        getCommand("backpack").setTabCompleter(backpackCommand);
        EventManager eventManager = EventManager.getInstance();
        EventCommand eventCommand = new EventCommand(eventManager);
        getCommand("event").setExecutor(eventCommand);
        getCommand("event").setTabCompleter(eventCommand);
        ForagingManager foragingManager = ForagingManager.getInstance();
        foragingManager.load(getDataFolder());
        ForagingCommand foragingCommand = new ForagingCommand(foragingManager);
        getCommand("foraging").setExecutor(foragingCommand);
        getCommand("foraging").setTabCompleter(foragingCommand);
        CombatManager combatManager = CombatManager.getInstance();
        CombatCommand combatCommand = new CombatCommand(combatManager);
        getCommand("combat").setExecutor(combatCommand);
        getCommand("combat").setTabCompleter(combatCommand);
        CollectionsCommand collectionsCommand = new CollectionsCommand(collectionManager);
        getCommand("collections").setExecutor(collectionsCommand);
        getCommand("collections").setTabCompleter(collectionsCommand);
        if (getCommand("collection") != null) {
            getCommand("collection").setExecutor(collectionsCommand);
            getCommand("collection").setTabCompleter(collectionsCommand);
        }
        getServer().getPluginManager().registerEvents(new CollectionListener(collectionManager), this);
        ChatManager chatManager = ChatManager.getInstance();
        ChatCommand chatCommand = new ChatCommand(chatManager);
        getCommand("chat").setExecutor(chatCommand);
        getCommand("chat").setTabCompleter(chatCommand);
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        LeaderboardCommand leaderboardCommand = new LeaderboardCommand(leaderboardManager);
        getCommand("leaderboard").setExecutor(leaderboardCommand);
        getCommand("leaderboard").setTabCompleter(leaderboardCommand);
        MailManager mailManager = MailManager.getInstance();
        mailManager.load(getDataFolder());
        MailCommand mailCommand = new MailCommand(mailManager);
        if (getCommand("mail") != null) {
            getCommand("mail").setExecutor(mailCommand);
            getCommand("mail").setTabCompleter(mailCommand);
        }
        ForgeCommand forgeCommand = new ForgeCommand(forgeManager);
        getCommand("forge").setExecutor(forgeCommand);
        getCommand("forge").setTabCompleter(forgeCommand);
        CalendarCommand calendarCommand = new CalendarCommand(calendarManager);
        if (getCommand("calendar") != null) {
            getCommand("calendar").setExecutor(calendarCommand);
            getCommand("calendar").setTabCompleter(calendarCommand);
        }
        CoopManager coopManager = CoopManager.getInstance();
        coopManager.load(getDataFolder());
        CoopCommand coopCommand = new CoopCommand(coopManager);
        getCommand("coop").setExecutor(coopCommand);
        getCommand("coop").setTabCompleter(coopCommand);
        ReputationManager reputationManager = ReputationManager.getInstance();
        reputationManager.load(getDataFolder());
        CrimsonCommand crimsonCommand = new CrimsonCommand(reputationManager);
        getCommand("crimson").setExecutor(crimsonCommand);
        getCommand("crimson").setTabCompleter(crimsonCommand);
        // Canonical Crimson Isle coordinator over faction reputation + Kuudra tiers.
        CrimsonIsleManager.getInstance();
        VaultManager vaultManager = VaultManager.getInstance();
        vaultManager.load(getDataFolder());
        VaultCommand vaultCommand = new VaultCommand(vaultManager);
        getCommand("vault").setExecutor(vaultCommand);
        getCommand("vault").setTabCompleter(vaultCommand);
        FriendManager friendManager = FriendManager.getInstance();
        friendManager.load(getDataFolder());
        FriendCommand friendCommand = new FriendCommand(friendManager);
        getCommand("friend").setExecutor(friendCommand);
        getCommand("friend").setTabCompleter(friendCommand);
        BoosterManager boosterManager = BoosterManager.getInstance();
        boosterManager.load(getDataFolder());
        BoosterCommand boosterCommand = new BoosterCommand(boosterManager);
        getCommand("booster").setExecutor(boosterCommand);
        getCommand("booster").setTabCompleter(boosterCommand);
        MailboxManager mailboxManager = MailboxManager.getInstance();
        mailboxManager.load(getDataFolder());
        MailboxCommand mailboxCommand = new MailboxCommand(mailboxManager);
        getCommand("mailbox").setExecutor(mailboxCommand);
        getCommand("mailbox").setTabCompleter(mailboxCommand);
        WarpManager warpManager = WarpManager.getInstance();
        warpManager.load(getDataFolder());
        WarpCommand warpCommand = new WarpCommand(warpManager);
        getCommand("warp").setExecutor(warpCommand);
        getCommand("warp").setTabCompleter(warpCommand);
        AchievementManager achievementManager = AchievementManager.getInstance();
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
        SkyblockLevelManager skyblockLevelManager = SkyblockLevelManager.getInstance();
        SkyblockLevelCommand skyblockLevelCommand = new SkyblockLevelCommand(skyblockLevelManager);
        getCommand("skyblock-level").setExecutor(skyblockLevelCommand);
        getCommand("skyblock-level").setTabCompleter(skyblockLevelCommand);
        SkyblockMenuCommand menuCommand = new SkyblockMenuCommand();
        if (getCommand("menu") != null) {
            getCommand("menu").setExecutor(menuCommand);
            getCommand("menu").setTabCompleter(menuCommand);
        }
        if (getCommand("skyblock") != null) {
            getCommand("skyblock").setExecutor(menuCommand);
            getCommand("skyblock").setTabCompleter(menuCommand);
        }
        TitleManager titleManager = TitleManager.getInstance();
        titleManager.load(getDataFolder());
        TitleCommand titleCommand = new TitleCommand(titleManager);
        if (getCommand("title") != null) {
            getCommand("title").setExecutor(titleCommand);
            getCommand("title").setTabCompleter(titleCommand);
        }
        RunManager runManager = RunManager.getInstance();
        runManager.load(getDataFolder());
        RunCommand runCommand = new RunCommand(runManager);
        if (getCommand("run") != null) {
            getCommand("run").setExecutor(runCommand);
            getCommand("run").setTabCompleter(runCommand);
        }
        AlchemyManager alchemyManager = AlchemyManager.getInstance();
        alchemyManager.load(getDataFolder());
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
        getServer().getPluginManager().registerEvents(new RiftListener(riftManager), this);
        SackCommand sackCommand = new SackCommand(sackManager);
        if (getCommand("sack") != null) {
            getCommand("sack").setExecutor(sackCommand);
            getCommand("sack").setTabCompleter(sackCommand);
        }
        CrystalHollowsManager crystalHollowsManager = CrystalHollowsManager.getInstance();
        CrystalHollowsCommand crystalHollowsCommand = new CrystalHollowsCommand(crystalHollowsManager);
        getCommand("crystalhollows").setExecutor(crystalHollowsCommand);
        getCommand("crystalhollows").setTabCompleter(crystalHollowsCommand);
        StorageCommand storageCommand = new StorageCommand(storageManager);
        getCommand("storage").setExecutor(storageCommand);
        getCommand("storage").setTabCompleter(storageCommand);
        // Canonical managers without dedicated commands — initialize so their state loads/persists.
        FairySoulManager.getInstance();
        BestiaryManager.getInstance();
        HarpManager.getInstance().load(getDataFolder());
        JerryWorkshopManager.getInstance().load(getDataFolder());
    }

    @Override
    public void onDisable() {
        AuctionHouseManager.getInstance().save(getDataFolder());
        BankManager.getInstance().save(getDataFolder());
        MayorManager.getInstance().save(getDataFolder());
        BazaarManager.getInstance().save(getDataFolder());
        DungeonManager.getInstance().save(getDataFolder());
        SlayerManager.getInstance().save(getDataFolder());
        fishingManager.save(getDataFolder());
        GardenManager.getInstance().save(getDataFolder());
        HotmManager.getInstance().save(getDataFolder());
        KuudraManager.getInstance().save(getDataFolder());
        EnchantingManager.getInstance().save(getDataFolder());
        ReforgeManager.getInstance().save(getDataFolder());
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
        try {
            WarpManager.getInstance().save(getDataFolder());
            HarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save data: " + e.getMessage());
        }
        try {
            HarpManager.getInstance().save(getDataFolder());
        } catch (java.io.IOException e) {
            getLogger().severe("Failed to save harp data: " + e.getMessage());
        }
        instance = null;
    }
}
