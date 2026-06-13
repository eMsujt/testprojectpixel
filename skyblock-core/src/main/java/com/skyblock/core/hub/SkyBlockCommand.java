package com.skyblock.core.hub;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>With no arguments, prints a formatted help menu. With an argument,
 * delegates to the matching sub-handler registered at construction time.</p>
 */
public final class SkyBlockCommand implements TabExecutor {

    private final Map<String, TabExecutor> handlers;

    /**
     * @param handlers map of subcommand name (lower-case) to its handler —
     *                 e.g. {@code "garden" -> gardenCommand}
     */
    public SkyBlockCommand(Map<String, TabExecutor> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        if (args.length > 0) {
            TabExecutor handler = handlers.get(args[0].toLowerCase());
            if (handler != null) {
                String[] delegated = Arrays.copyOfRange(args, 1, args.length);
                return handler.onCommand(sender, command, args[0], delegated);
            }
            player.sendMessage("Unknown subcommand: " + args[0] + ". Use /sb for help.");
        }
        sendHelp(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.copyOf(handlers.keySet());
        }
        if (args.length > 1) {
            TabExecutor handler = handlers.get(args[0].toLowerCase());
            if (handler != null) {
                String[] delegated = Arrays.copyOfRange(args, 1, args.length);
                return handler.onTabComplete(sender, command, args[0], delegated);
            }
        }
        return Collections.emptyList();
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== SkyBlock Help ===");
        player.sendMessage("/sb garden               — manage your garden");
        player.sendMessage("/sb slayer               — view slayer quests and bosses");
        player.sendMessage("/sb pets                 — view and equip your pets");
        player.sendMessage("/sb skills               — view and manage your skills");
        player.sendMessage("/sb collections          — view your item collections");
        player.sendMessage("/sb enchanting           — view enchanting table and books");
        player.sendMessage("/sb hotm                 — view Heart of the Mountain perks");
        player.sendMessage("/sb kuudra               — view Kuudra tiers and rewards");
        player.sendMessage("/sb bank                 — manage your personal bank balance");
        player.sendMessage("/sb profile              — view your SkyBlock profile");
        player.sendMessage("/sb mayor                — view current mayor and perks");
        player.sendMessage("/sb auctionhouse         — browse and list on the Auction House");
        player.sendMessage("/sb bazaar               — browse and trade on the Bazaar");
        player.sendMessage("/sb dungeon              — view dungeon stats and classes");
        player.sendMessage("/sb island               — view and manage your private island");
        player.sendMessage("=== Use /sb for this menu ===");
    }
}
