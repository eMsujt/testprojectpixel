package com.skyblock.core.command;

import com.skyblock.core.achievement.AchievementCommand;
import com.skyblock.core.achievement.AchievementManager;
import com.skyblock.core.alchemy.AlchemyCommand;
import com.skyblock.core.alchemy.AlchemyManager;
import com.skyblock.core.auction.command.AuctionHouseCommand;
import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.backpack.BackpackCommand;
import com.skyblock.core.backpack.BackpackManager;
import com.skyblock.core.bank.command.BankCommand;
import com.skyblock.core.bazaar.command.BazaarCommand;
import com.skyblock.core.booster.BoosterCommand;
import com.skyblock.core.booster.BoosterManager;
import com.skyblock.core.chat.ChatCommand;
import com.skyblock.core.chat.ChatManager;
import com.skyblock.core.collections.command.CollectionsCommand;
import com.skyblock.core.combat.command.CombatCommand;
import com.skyblock.core.combat.manager.CombatManager;
import com.skyblock.core.coop.CoopCommand;
import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.crafting.CraftingCommand;
import com.skyblock.core.crafting.manager.CraftingManager;
import com.skyblock.core.crimson.CrimsonCommand;
import com.skyblock.core.dungeon.command.DungeonCommand;
import com.skyblock.core.dungeon.manager.RunManager;
import com.skyblock.core.enchanting.EnchantingCommand;
import com.skyblock.core.event.EventCommand;
import com.skyblock.core.fishing.command.FishingCommand;
import com.skyblock.core.fishing.manager.TrophyFishingManager;
import com.skyblock.core.foraging.ForagingCommand;
import com.skyblock.core.foraging.ForagingManager;
import com.skyblock.core.forge.ForgeCommand;
import com.skyblock.core.forge.ForgeManager;
import com.skyblock.core.friend.FriendCommand;
import com.skyblock.core.friend.FriendManager;
import com.skyblock.core.garden.GardenCommand;
import com.skyblock.core.guild.GuildCommand;
import com.skyblock.core.guild.GuildManager;
import com.skyblock.core.hotm.command.HOTMCommand;
import com.skyblock.core.kuudra.KuudraCommand;
import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.leaderboard.LeaderboardCommand;
import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.level.SkyblockLevelCommand;
import com.skyblock.core.level.SkyblockLevelManager;
import com.skyblock.core.mail.MailCommand;
import com.skyblock.core.mail.MailManager;
import com.skyblock.core.mailbox.MailboxCommand;
import com.skyblock.core.mailbox.MailboxManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BazaarManager;
import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.manager.EventManager;
import com.skyblock.core.manager.FishingManager;
import com.skyblock.core.manager.GardenManager;
import com.skyblock.core.manager.HotmManager;
import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.PartyManager;
import com.skyblock.core.manager.PetManager;
import com.skyblock.core.manager.QuestManager;
import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.ReputationManager;
import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.mayor.MayorCommand;
import com.skyblock.core.minion.command.MinionCommand;
import com.skyblock.core.party.PartyCommand;
import com.skyblock.core.pet.PetCommand;
import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.quest.command.QuestCommand;
import com.skyblock.core.reforge.ReforgeCommand;
import com.skyblock.core.run.RunCommand;
import com.skyblock.core.skills.command.SkillsCommand;
import com.skyblock.core.slayer.command.SlayerCommand;
import com.skyblock.core.stat.StatCommand;
import com.skyblock.core.stat.StatManager;
import com.skyblock.core.stats.StatsCommand;
import com.skyblock.core.title.TitleCommand;
import com.skyblock.core.title.TitleManager;
import com.skyblock.core.trade.TradeCommand;
import com.skyblock.core.trade.TradeManager;
import com.skyblock.core.vault.VaultCommand;
import com.skyblock.core.vault.VaultManager;
import com.skyblock.core.wardrobe.WardrobeCommand;
import com.skyblock.core.wardrobe.WardrobeManager;
import com.skyblock.core.warp.WarpCommand;
import com.skyblock.core.warp.WarpManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Single source of truth for wiring SkyBlock command executors to their
 * declared commands.
 *
 * <p>Each binding is guarded against commands the host plugin's {@code
 * plugin.yml} does not declare, so the same registry can serve any plugin
 * entry point — it only wires the commands that actually exist.</p>
 */
public final class CommandRegistry {

    private CommandRegistry() {
    }

    /**
     * Wires every canonical command executor to the matching command declared
     * by the given plugin. Commands not declared by the plugin are skipped.
     *
     * @param plugin the plugin whose commands should be wired
     */
    public static void registerAll(JavaPlugin plugin) {
        bind(plugin, new SkyblockMenuCommand(), "skyblock", "menu");
        bind(plugin, new BankCommand(BankManager.getInstance()), "bank");
        bind(plugin, new MayorCommand(MayorManager.getInstance()), "mayor");
        bind(plugin, new AuctionHouseCommand(AuctionHouseManager.getInstance()), "auctionhouse", "ah");
        bind(plugin, new BazaarCommand(BazaarManager.getInstance()), "bazaar");
        bind(plugin, new DungeonCommand(DungeonManager.getInstance()), "dungeon");
        bind(plugin, new GuildCommand(GuildManager.getInstance()), "guild");
        bind(plugin, new PartyCommand(PartyManager.getInstance()), "party");
        bind(plugin, new GardenCommand(GardenManager.getInstance()), "garden");
        bind(plugin, new SlayerCommand(SlayerManager.getInstance()), "slayer", "slay");
        bind(plugin, new FishingCommand(FishingManager.getInstance(), TrophyFishingManager.getInstance()), "fishing");
        bind(plugin, new HOTMCommand(HotmManager.getInstance()), "hotm", "hotmtree");
        bind(plugin, new KuudraCommand(KuudraManager.getInstance()), "kuudra");
        bind(plugin, new EnchantingCommand(EnchantingManager.getInstance()), "enchanting");
        bind(plugin, new ReforgeCommand(ReforgeManager.getInstance()), "reforge");
        bind(plugin, new StatsCommand(SkillManager.getInstance(), SlayerManager.getInstance()), "stats");
        bind(plugin, new SkillsCommand(SkillManager.getInstance()), "skills");
        bind(plugin, new ProfileCommand(ProfileManager.getInstance()), "profile");
        bind(plugin, new MinionCommand(MinionManager.getInstance()), "minion");
        bind(plugin, new CraftingCommand(CraftingManager.getInstance()), "crafting");
        bind(plugin, new QuestCommand(QuestManager.getInstance()), "quest");
        bind(plugin, new TradeCommand(TradeManager.getInstance()), "trade");
        bind(plugin, new BackpackCommand(BackpackManager.getInstance()), "backpack");
        bind(plugin, new EventCommand(EventManager.getInstance()), "event");
        bind(plugin, new ForagingCommand(ForagingManager.getInstance()), "foraging");
        bind(plugin, new CombatCommand(CombatManager.getInstance()), "combat");
        bind(plugin, new CollectionsCommand(CollectionManager.getInstance()), "collections");
        bind(plugin, new ChatCommand(ChatManager.getInstance()), "chat");
        bind(plugin, new LeaderboardCommand(LeaderboardManager.getInstance()), "leaderboard");
        bind(plugin, new MailCommand(MailManager.getInstance()), "mail");
        bind(plugin, new ForgeCommand(ForgeManager.getInstance()), "forge");
        bind(plugin, new CoopCommand(CoopManager.getInstance()), "coop");
        bind(plugin, new CrimsonCommand(ReputationManager.getInstance()), "crimson");
        bind(plugin, new VaultCommand(VaultManager.getInstance()), "vault");
        bind(plugin, new FriendCommand(FriendManager.getInstance()), "friend");
        bind(plugin, new BoosterCommand(BoosterManager.getInstance()), "booster");
        bind(plugin, new MailboxCommand(MailboxManager.getInstance()), "mailbox");
        bind(plugin, new WarpCommand(WarpManager.getInstance()), "warp");
        bind(plugin, new AchievementCommand(AchievementManager.getInstance()), "achievement");
        bind(plugin, new StatCommand(StatManager.getInstance()), "stat");
        bind(plugin, new SkyblockLevelCommand(SkyblockLevelManager.getInstance()), "skyblock-level");
        bind(plugin, new TitleCommand(TitleManager.getInstance()), "title");
        bind(plugin, new RunCommand(RunManager.getInstance()), "run");
        bind(plugin, new AlchemyCommand(AlchemyManager.getInstance()), "alchemy");
        bind(plugin, new WardrobeCommand(WardrobeManager.getInstance()), "wardrobe");
        bind(plugin, new PetCommand(PetManager.getInstance()), "pets", "pet");
    }

    private static void bind(JavaPlugin plugin, CommandExecutor command, String... names) {
        for (String name : names) {
            PluginCommand pc = plugin.getCommand(name);
            if (pc == null) {
                continue;
            }
            pc.setExecutor(command);
            if (command instanceof TabCompleter tab) {
                pc.setTabCompleter(tab);
            }
        }
    }
}
