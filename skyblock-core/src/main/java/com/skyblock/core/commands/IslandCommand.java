package com.skyblock.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the {@code /is} command and dispatches subcommands.
 *
 * <p>Supported subcommands:
 * <ul>
 *   <li>{@code /is go} — teleports the sender to their own island</li>
 *   <li>{@code /is visit <player>} — teleports the sender to another player's island</li>
 *   <li>{@code /is help} — lists available subcommands</li>
 * </ul>
 * </p>
 */
public final class IslandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        String sub = args.length > 0 ? args[0].toLowerCase() : "help";

        switch (sub) {
            case "go" -> goToIsland(player, label);
            case "visit" -> visitIsland(player, label, args);
            case "help" -> sendHelp(player, label);
            default -> player.sendMessage("Unknown subcommand. Use /" + label + " help for a list of commands.");
        }

        return true;
    }

    private void goToIsland(Player player, String label) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.sendMessage("Teleporting you to your island...");
    }

    private void visitIsland(Player player, String label, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /" + label + " visit <player>");
            return;
        }
        String targetName = args[1];
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage("Player '" + targetName + "' is not online.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("Use /" + label + " go to visit your own island.");
            return;
        }
        player.teleport(target.getWorld().getSpawnLocation());
        player.sendMessage("Teleporting you to " + target.getName() + "'s island...");
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/" + label + " go              - Teleport to your island");
        player.sendMessage("/" + label + " visit <player>  - Visit another player's island");
        player.sendMessage("/" + label + " help            - Show this help message");
    }
}
