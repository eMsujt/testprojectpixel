package com.skyblock.core.backpack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /backpack} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /backpack create <name>} — create a new empty backpack</li>
 *   <li>{@code /backpack delete <name>} — delete a backpack</li>
 *   <li>{@code /backpack list}          — list all owned backpacks</li>
 * </ul>
 * </p>
 */
public final class BackpackCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("create", "delete", "list");

    private final BackpackManager backpackManager;

    public BackpackCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "list"   -> handleList(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && sender instanceof Player player) {
            String sub = args[0].toLowerCase();
            if (sub.equals("delete")) {
                String prefix = args[1].toLowerCase();
                return backpackManager.getBackpackNames(player.getUniqueId()).stream()
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /backpack create <name>");
            return;
        }
        String name = args[1];
        boolean created = backpackManager.createBackpack(player.getUniqueId(), name);
        if (created) {
            player.sendMessage("Backpack '" + name + "' created.");
        } else {
            Set<String> names = backpackManager.getBackpackNames(player.getUniqueId());
            if (names.contains(name)) {
                player.sendMessage("A backpack named '" + name + "' already exists.");
            } else {
                player.sendMessage("You have reached the maximum of " + BackpackManager.MAX_BACKPACKS + " backpacks.");
            }
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /backpack delete <name>");
            return;
        }
        String name = args[1];
        boolean removed = backpackManager.deleteBackpack(player.getUniqueId(), name);
        if (removed) {
            player.sendMessage("Backpack '" + name + "' deleted.");
        } else {
            player.sendMessage("No backpack named '" + name + "' found.");
        }
    }

    private void handleList(Player player) {
        Set<String> names = backpackManager.getBackpackNames(player.getUniqueId());
        if (names.isEmpty()) {
            player.sendMessage("You have no backpacks.");
            return;
        }
        player.sendMessage("=== Your Backpacks (" + names.size() + "/" + BackpackManager.MAX_BACKPACKS + ") ===");
        for (String name : names) {
            player.sendMessage("  - " + name);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Backpack Commands ===");
        player.sendMessage("/backpack create <name> — create a new backpack");
        player.sendMessage("/backpack delete <name> — delete a backpack");
        player.sendMessage("/backpack list          — list all your backpacks");
    }
}
