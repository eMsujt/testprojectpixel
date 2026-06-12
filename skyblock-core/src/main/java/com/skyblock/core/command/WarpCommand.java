package com.skyblock.core.command;

import com.skyblock.core.warp.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /warp} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /warp}               — lists available warps</li>
 *   <li>{@code /warp <name>}        — teleports to the named warp</li>
 *   <li>{@code /warp set <name>}    — sets a warp at the player's location (requires {@code skyblock.warp.set})</li>
 *   <li>{@code /warp delete <name>} — removes a named warp (requires {@code skyblock.warp.set})</li>
 * </ul>
 * </p>
 */
public final class WarpCommand implements TabExecutor {

    private final WarpManager warpManager;

    public WarpCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendWarpList(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("set")) {
            if (!player.hasPermission("skyblock.warp.set")) {
                player.sendMessage("You don't have permission to set warps.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage("Usage: /warp set <name>");
                return true;
            }
            String name = args[1].toLowerCase();
            warpManager.setWarp(name, player.getLocation());
            player.sendMessage("Warp '" + name + "' has been set.");
            return true;
        }

        if (sub.equals("delete") || sub.equals("del")) {
            if (!player.hasPermission("skyblock.warp.set")) {
                player.sendMessage("You don't have permission to delete warps.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage("Usage: /warp delete <name>");
                return true;
            }
            String name = args[1].toLowerCase();
            if (warpManager.removeWarp(name)) {
                player.sendMessage("Warp '" + name + "' has been deleted.");
            } else {
                player.sendMessage("No warp found with name '" + name + "'.");
            }
            return true;
        }

        Optional<Location> dest = warpManager.getWarp(sub);
        if (dest.isEmpty()) {
            player.sendMessage("Unknown warp: " + sub + ". Use /warp to see all warps.");
            return true;
        }
        player.teleport(dest.get());
        player.sendMessage("Warped to " + sub + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> options = new ArrayList<>(warpManager.getWarpNames());
            options.add("set");
            options.add("delete");
            return options.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del"))) {
            String prefix = args[1].toLowerCase();
            Set<String> names = warpManager.getWarpNames();
            return names.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendWarpList(Player player) {
        Set<String> names = warpManager.getWarpNames();
        if (names.isEmpty()) {
            player.sendMessage("No warps available.");
            return;
        }
        player.sendMessage("=== Available Warps ===");
        for (String name : names) {
            player.sendMessage("- " + name);
        }
    }
}
