package com.skyblock.core.command;

import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /skyblock}        — open the SkyBlock main menu</li>
 *   <li>{@code /skyblock help}   — list every feature and command</li>
 *   <li>{@code /skyblock version} — show the plugin version</li>
 * </ul>
 * </p>
 */
public final class SkyBlockCommand implements TabExecutor {

    private static final List<String> HELP_LINES = Arrays.asList(
            ChatColor.GOLD + "=== SkyBlock Commands ===",
            ChatColor.YELLOW + "/island" + ChatColor.GRAY + " — manage your SkyBlock island",
            ChatColor.YELLOW + "/islandupgrade" + ChatColor.GRAY + " — purchase island upgrades",
            ChatColor.YELLOW + "/skills / /skill" + ChatColor.GRAY + " — view skill levels and XP",
            ChatColor.YELLOW + "/pets / /pet" + ChatColor.GRAY + " — manage your pets",
            ChatColor.YELLOW + "/auction / /auctionhouse" + ChatColor.GRAY + " — Auction House",
            ChatColor.YELLOW + "/bazaar" + ChatColor.GRAY + " — open the Bazaar",
            ChatColor.YELLOW + "/profile" + ChatColor.GRAY + " — view and manage profiles",
            ChatColor.YELLOW + "/bank / /banking" + ChatColor.GRAY + " — manage your bank account",
            ChatColor.YELLOW + "/slay" + ChatColor.GRAY + " — manage slayer quests",
            ChatColor.YELLOW + "/dungeon" + ChatColor.GRAY + " — dungeon management",
            ChatColor.YELLOW + "/collection" + ChatColor.GRAY + " — view collection progress",
            ChatColor.YELLOW + "/quest" + ChatColor.GRAY + " — manage quests",
            ChatColor.YELLOW + "/essence" + ChatColor.GRAY + " — view and manage essence",
            ChatColor.YELLOW + "/hotm" + ChatColor.GRAY + " — Heart of the Mountain perks",
            ChatColor.YELLOW + "/crimsonisle" + ChatColor.GRAY + " — Crimson Isle faction and Kuudra",
            ChatColor.YELLOW + "/mining" + ChatColor.GRAY + " — mining stats and XP",
            ChatColor.YELLOW + "/fishing" + ChatColor.GRAY + " — fishing stats and trophy fish",
            ChatColor.YELLOW + "/forge / /itemforge" + ChatColor.GRAY + " — forge items",
            ChatColor.YELLOW + "/alchemy" + ChatColor.GRAY + " — brew potions",
            ChatColor.YELLOW + "/enchant / /enchanting / /enchantment" + ChatColor.GRAY + " — enchantments",
            ChatColor.YELLOW + "/talisman" + ChatColor.GRAY + " — manage talismans",
            ChatColor.YELLOW + "/accessorybag" + ChatColor.GRAY + " — accessory bag",
            ChatColor.YELLOW + "/crafting" + ChatColor.GRAY + " — crafting recipes",
            ChatColor.YELLOW + "/minion" + ChatColor.GRAY + " — manage minions",
            ChatColor.YELLOW + "/npc" + ChatColor.GRAY + " — NPC shops",
            ChatColor.YELLOW + "/shop" + ChatColor.GRAY + " — browse the shop",
            ChatColor.YELLOW + "/warp" + ChatColor.GRAY + " — teleport to warps",
            ChatColor.YELLOW + "/leaderboard" + ChatColor.GRAY + " — view leaderboards",
            ChatColor.YELLOW + "/dailyreward" + ChatColor.GRAY + " — claim your daily reward",
            ChatColor.YELLOW + "/storage" + ChatColor.GRAY + " — manage personal storage",
            ChatColor.YELLOW + "/trade" + ChatColor.GRAY + " — trade with another player",
            ChatColor.YELLOW + "/wardrobe" + ChatColor.GRAY + " — save and load armor outfits",
            ChatColor.YELLOW + "/bestiary" + ChatColor.GRAY + " — track mob kill counts",
            ChatColor.YELLOW + "/party" + ChatColor.GRAY + " — manage your party",
            ChatColor.YELLOW + "/f" + ChatColor.GRAY + " — manage friendships"
    );

    private final SkyBlockMenuManager menuManager;

    public SkyBlockCommand(SkyBlockMenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return true;
            }
            menuManager.openMainMenu(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "help" -> {
                for (String line : HELP_LINES) {
                    sender.sendMessage(line);
                }
            }
            case "version" -> sender.sendMessage(ChatColor.GOLD + "SkyBlock" + ChatColor.GRAY + " version " + ChatColor.WHITE + "1.0");
            default -> sender.sendMessage(ChatColor.RED + "Unknown sub-command. Use /skyblock help for a list of commands.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("help", "version").stream()
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
