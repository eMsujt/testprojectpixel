package com.skyblock.core.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /island} command and its tab-completion.
 *
 * <p>Supported subcommands:
 * <ul>
 *   <li>{@code /island create} — creates the player's island</li>
 *   <li>{@code /island home} — teleports the player to their island</li>
 *   <li>{@code /island visit <player>} — visits another player's island</li>
 *   <li>{@code /island warp <player>} — warps to another player's public island</li>
 *   <li>{@code /island help} — lists available subcommands</li>
 * </ul>
 * </p>
 */
public final class IslandCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("create", "home", "visit", "warp", "help");

    private final Map<UUID, Boolean> islandOwners = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        String sub = args.length > 0 ? args[0].toLowerCase() : "help";

        switch (sub) {
            case "create" -> handleCreate(player);
            case "home" -> handleHome(player, label);
            case "visit" -> handleVisit(player, args, label);
            case "warp" -> handleWarp(player, args, label);
            default -> sendHelp(player, label);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("visit") || sub.equals("warp")) {
                String partial = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(partial))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private void handleCreate(Player player) {
        UUID id = player.getUniqueId();
        if (islandOwners.containsKey(id)) {
            player.sendMessage("You already have an island!");
            return;
        }
        islandOwners.put(id, true);
        player.sendMessage("Your island has been created! Use /island home to go there.");
    }

    private void handleHome(Player player, String label) {
        UUID id = player.getUniqueId();
        if (!islandOwners.containsKey(id)) {
            player.sendMessage("You don't have an island yet. Use /" + label + " create to make one.");
            return;
        }
        player.sendMessage("Teleporting to your island...");
        player.teleport(player.getWorld().getSpawnLocation());
    }

    private void handleVisit(Player player, String[] args, String label) {
        if (args.length < 2) {
            player.sendMessage("Usage: /" + label + " visit <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!islandOwners.containsKey(target.getUniqueId())) {
            player.sendMessage(target.getName() + " does not have an island.");
            return;
        }
        player.sendMessage("Visiting " + target.getName() + "'s island...");
        player.teleport(target.getWorld().getSpawnLocation());
    }

    private void handleWarp(Player player, String[] args, String label) {
        if (args.length < 2) {
            player.sendMessage("Usage: /" + label + " warp <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!islandOwners.containsKey(target.getUniqueId())) {
            player.sendMessage(target.getName() + " does not have an island.");
            return;
        }
        player.sendMessage("Warping to " + target.getName() + "'s island...");
        player.teleport(target.getWorld().getSpawnLocation());
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/" + label + " create        - Create your island");
        player.sendMessage("/" + label + " home          - Teleport to your island");
        player.sendMessage("/" + label + " visit <name>  - Visit another player's island");
        player.sendMessage("/" + label + " warp <name>   - Warp to another player's island");
        player.sendMessage("/" + label + " help          - Show this help message");
    }
}
