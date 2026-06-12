package com.skyblock.core;

import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.auction.AuctionCommand;
import com.skyblock.core.auction.AuctionHouseManager;
import com.skyblock.core.auction.AuctionManager;
import com.skyblock.core.island.IslandCommand;
import com.skyblock.core.island.IslandManager;
import com.skyblock.core.bazaar.BazaarManager;
import com.skyblock.core.collections.CollectionManager;
import com.skyblock.core.combat.CombatManager;
import com.skyblock.core.combat.StatManager;
import com.skyblock.core.command.LeaderboardCommand;
import com.skyblock.core.command.ShopCommand;
import com.skyblock.core.command.SkyBlockMenuCommand;
import com.skyblock.core.command.SkillsCommand;
import com.skyblock.core.command.WarpCommand;
import com.skyblock.core.commands.SkyBlockCommand;
import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.enchanting.EnchantmentManager;
import com.skyblock.core.fishing.FishingManager;
import com.skyblock.core.listener.CoreListeners;
import com.skyblock.core.listeners.SkyBlockEventListener;
import com.skyblock.core.magic.FairySoulManager;
import com.skyblock.core.menu.MenuManager;
import com.skyblock.core.minion.MinionManager;
import com.skyblock.core.pets.PetManager;
import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.quests.QuestManager;
import com.skyblock.core.scoreboard.ScoreboardManager;
import com.skyblock.core.shop.ShopManager;
import com.skyblock.core.skills.SkillManager;
import com.skyblock.core.warps.WarpManager;
import com.skyblock.farming.FarmingManager;
import com.skyblock.foraging.ForagingManager;
import com.skyblock.mining.MiningManager;
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
        MiningManager miningManager = MiningManager.getInstance();
        FarmingManager farmingManager = new FarmingManager();
        ForagingManager foragingManager = new ForagingManager();

        // initialise all singletons so they are ready before commands fire
        EconomyManager.getInstance();
        SkillManager skillManager = SkillManager.getInstance();
        AuctionHouseManager.getInstance();
        AuctionManager auctionManager = AuctionManager.getInstance();
        getCommand("auction").setExecutor(new AuctionCommand(auctionManager));
        BazaarManager.getInstance();
        CollectionManager.getInstance();
        EnchantmentManager.getInstance();
        FairySoulManager.getInstance();
        MinionManager.getInstance();
        PetManager.getInstance();
        ProfileManager.getInstance();
        QuestManager.getInstance();
        ScoreboardManager.getInstance();
        ShopManager.getInstance();
        StatManager.getInstance();
        WarpManager.getInstance();
        FishingManager fishingManager = FishingManager.getInstance();

        IslandManager.getInstance();
        getCommand("island").setExecutor(new IslandCommand(IslandManager.getInstance()));
        getCommand("skyblock").setExecutor(new SkyBlockMenuCommand(MenuManager.getInstance()));
        getCommand("skills").setExecutor(new SkillsCommand(skillManager));
        com.skyblock.core.warp.WarpManager warpManager = com.skyblock.core.warp.WarpManager.getInstance();
        warpManager.load(new java.io.File(getDataFolder(), "warps.yml"));
        getCommand("warp").setExecutor(new WarpCommand(warpManager));
        getCommand("shop").setExecutor(new ShopCommand(ShopManager.getInstance(), EconomyManager.getInstance()));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(LeaderboardManager.getInstance()));
        getServer().getPluginManager().registerEvents(
                new SkyBlockEventListener(miningManager, farmingManager, foragingManager), this);
        getServer().getPluginManager().registerEvents(CombatManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(fishingManager, this);
        getServer().getPluginManager().registerEvents(MenuManager.getInstance(), this);
        getServer().getPluginManager().registerEvents(new CoreListeners(playerDataManager), this);

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
        getLogger().info("SkyBlock core disabled.");
        instance = null;
    }
}
