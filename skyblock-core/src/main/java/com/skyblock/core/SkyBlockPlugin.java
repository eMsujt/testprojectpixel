package com.skyblock.core;

import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.alchemy.AlchemyListener;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.auction.AuctionCommand;
import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.auction.AuctionManager;
import com.skyblock.core.bank.BankManager;
import com.skyblock.core.island.IslandCommand;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.bazaar.BazaarManager;
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
import com.skyblock.core.crafting.CraftingManager;
import com.skyblock.core.crafting.SkyBlockRecipeManager;
import com.skyblock.core.dungeon.DungeonManager;
import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.enchant.EnchantCommand;
import com.skyblock.core.enchanting.EnchantingCommand;
import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.enchanting.EnchantmentManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.forge.ForgeCommand;
import com.skyblock.core.forge.ForgeManager;
import com.skyblock.core.listener.CoreListeners;
import com.skyblock.core.magic.FairySoulManager;
import com.skyblock.core.menu.MenuManager;
import com.skyblock.core.minion.MinionCommand;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.pets.PetManager;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.npc.NpcCommand;
import com.skyblock.core.npc.NpcManager;
import com.skyblock.core.command.QuestCommand;
import com.skyblock.core.quests.QuestManager;
import com.skyblock.core.scoreboard.ScoreboardManager;
import com.skyblock.core.shop.ShopManager;
import com.skyblock.core.skill.SkillCommand;
import com.skyblock.core.skills.SkillManager;
import com.skyblock.core.stats.CombatStatsListener;
import com.skyblock.core.stats.CombatStatsManager;
import com.skyblock.core.storage.YamlPlayerStorage;
import com.skyblock.core.talisman.TalismanCommand;
import com.skyblock.core.talisman.TalismanManager;
import com.skyblock.core.warps.WarpManager;
import com.skyblock.core.foraging.ForagingListener;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.mining.MiningListener;
import com.skyblock.core.mining.MiningManager;
import com.skyblock.core.farming.FarmingListener;
import com.skyblock.core.farming.FarmingManager;
import com.skyblock.core.mob.MobManager;
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
        AuctionHouseManager.getInstance();
        AuctionManager auctionManager = AuctionManager.getInstance();
        getCommand("auction").setExecutor(new AuctionCommand(auctionManager));
        BazaarManager bazaarManager = BazaarManager.getInstance();
        getCommand("bazaar").setExecutor(new BazaarCommand(bazaarManager));
        BankManager bankManager = BankManager.getInstance();
        getCommand("bank").setExecutor(new BankCommand(bankManager));
        CollectionManager collectionManager = CollectionManager.getInstance();
        getCommand("collection").setExecutor(new CollectionCommand(collectionManager));
        DungeonManager dungeonManager = DungeonManager.getInstance();
        getCommand("dungeon").setExecutor(new DungeonCommand(dungeonManager));
        EnchantmentManager.getInstance();
        EnchantingManager enchantingManager = EnchantingManager.getInstance();
        getCommand("enchanting").setExecutor(new EnchantingCommand(enchantingManager));
        com.skyblock.core.enchant.EnchantManager enchantManager = com.skyblock.core.enchant.EnchantManager.getInstance();
        EnchantCommand enchantCommand = new EnchantCommand(enchantManager);
        getCommand("enchant").setExecutor(enchantCommand);
        getCommand("enchant").setTabCompleter(enchantCommand);
        FairySoulManager.getInstance();
        ForgeManager forgeManager = ForgeManager.getInstance();
        getCommand("forge").setExecutor(new ForgeCommand(forgeManager));
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        AlchemyManager alchemyManager = AlchemyManager.getInstance();
        getCommand("alchemy").setExecutor(new AlchemyCommand(alchemyManager));
        MinionManager minionManager = MinionManager.getInstance();
        getCommand("minion").setExecutor(new MinionCommand(minionManager));
        PetManager petManager = PetManager.getInstance();
        getCommand("pets").setExecutor(new PetsCommand(petManager));
        ProfileManager profileManager = ProfileManager.getInstance();
        getCommand("profile").setExecutor(new ProfileCommand(profileManager));
        NpcManager npcManager = NpcManager.getInstance();
        getCommand("npc").setExecutor(new NpcCommand(npcManager, economyManager));
        QuestCommand questCommand = new QuestCommand(QuestManager.getInstance());
        getCommand("quest").setExecutor(questCommand);
        getCommand("quest").setTabCompleter(questCommand);
        ScoreboardManager.getInstance().start(this);
        ShopManager shopManager = ShopManager.getInstance();
        SkyBlockRecipeManager.getInstance();
        CraftingManager craftingManager = CraftingManager.getInstance();
        getCommand("crafting").setExecutor(new CraftingCommand(craftingManager));
        StatManager.getInstance();
        TalismanManager talismanManager = TalismanManager.getInstance();
        getCommand("talisman").setExecutor(new TalismanCommand(talismanManager));
        WarpManager.getInstance();
        FishingManager fishingManager = FishingManager.getInstance();
        YamlPlayerStorage.getInstance();

        IslandManager.getInstance();
        getCommand("island").setExecutor(new IslandCommand(IslandManager.getInstance()));
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
        getServer().getPluginManager().registerEvents(new MiningListener(miningManager), this);
        getServer().getPluginManager().registerEvents(new ForagingListener(foragingManager), this);
        getServer().getPluginManager().registerEvents(new FarmingListener(farmingManager), this);
        getServer().getPluginManager().registerEvents(new AlchemyListener(alchemyManager), this);
        getServer().getPluginManager().registerEvents(new CombatListener(CombatManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new com.skyblock.core.fishing.FishingListener(fishingManager), this);
        getServer().getPluginManager().registerEvents(MenuManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(new CoreListeners(playerDataManager), this);
        CombatStatsManager combatStatsManager = CombatStatsManager.getInstance();
        getServer().getPluginManager().registerEvents(new CombatStatsListener(combatStatsManager), this);

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
