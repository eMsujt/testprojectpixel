package com.skyblock.core.gemstone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /gemstone} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /gemstone info}                     — show your total gemstone count</li>
 *   <li>{@code /gemstone view <type>}              — show your count for a specific gemstone</li>
 *   <li>{@code /gemstone types}                    — list all gemstone types</li>
 *   <li>{@code /gemstone add <type> <amount>}      — (op) add gemstones to your collection</li>
 * </ul>
 * </p>
 */
public final class GemstoneCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "info", "view", "types", "add");

    private final GemstoneManager gemstoneManager;

    public GemstoneCommand(GemstoneManager gemstoneManager) {
        this.gemstoneManager = gemstoneManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /gemstone <info|view|types|add>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "view"  -> handleView(player, args);
            case "types" -> handleTypes(player);
            case "add"   -> handleAdd(player, args);
            default      -> player.sendMessage(
                    "Unknown subcommand. Usage: /gemstone <info|view|types|add>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("view")
                || args[0].equalsIgnoreCase("add"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(GemstoneManager.GemstoneType.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int total = gemstoneManager.getTotalCount(player.getUniqueId());
        player.sendMessage("Gemstones collected: " + total + " total.");
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /gemstone view <type>");
            return;
        }
        GemstoneManager.GemstoneType type;
        try {
            type = GemstoneManager.GemstoneType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown gemstone type: " + args[1]);
            return;
        }
        int count = gemstoneManager.getCount(player.getUniqueId(), type);
        player.sendMessage(type.getDisplayName() + ": " + count);
    }

    private void handleTypes(Player player) {
        player.sendMessage("=== Gemstone Types ===");
        Map<GemstoneManager.GemstoneType, Integer> counts =
                gemstoneManager.getAllCounts(player.getUniqueId());
        for (GemstoneManager.GemstoneType type : GemstoneManager.GemstoneType.values()) {
            int count = counts.getOrDefault(type, 0);
            player.sendMessage(type.getDisplayName() + ": " + count);
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /gemstone add <type> <amount>");
            return;
        }
        GemstoneManager.GemstoneType type;
        try {
            type = GemstoneManager.GemstoneType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown gemstone type: " + args[1]);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        int newCount = gemstoneManager.addGemstone(player.getUniqueId(), type, amount);
        player.sendMessage("Added " + amount + " " + type.getDisplayName()
                + "(s). New total: " + newCount + ".");
    }
}
