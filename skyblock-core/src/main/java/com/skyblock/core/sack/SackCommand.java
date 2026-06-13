package com.skyblock.core.sack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /sack} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /sack info}                     — show your total item count across all sacks</li>
 *   <li>{@code /sack view <type>}               — show item count for a specific sack type</li>
 *   <li>{@code /sack types}                     — list all sack types</li>
 *   <li>{@code /sack add <type> <amount>}       — (op) add items to a sack type</li>
 * </ul>
 * </p>
 */
public final class SackCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "info", "view", "types", "add");

    private final SackManager sackManager;

    public SackCommand(SackManager sackManager) {
        this.sackManager = sackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /sack <info|view|types|add>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "view"  -> handleView(player, args);
            case "types" -> handleTypes(player);
            case "add"   -> handleAdd(player, args);
            default      -> player.sendMessage(
                    "Unknown subcommand. Usage: /sack <info|view|types|add>");
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
            return Arrays.stream(SackManager.SackType.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int total = sackManager.getTotalItems(player.getUniqueId());
        player.sendMessage("Sack items: " + total + " item(s) stored across all sacks.");
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /sack view <type>");
            return;
        }
        SackManager.SackType sackType;
        try {
            sackType = SackManager.SackType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown sack type: " + args[1]);
            return;
        }
        int count = sackManager.getCount(player.getUniqueId(), sackType);
        player.sendMessage("=== " + sackType.getDisplayName() + " ===");
        player.sendMessage("Items stored: " + count);
    }

    private void handleTypes(Player player) {
        player.sendMessage("=== Sack Types ===");
        for (SackManager.SackType sackType : SackManager.SackType.values()) {
            player.sendMessage(sackType.getDisplayName());
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /sack add <type> <amount>");
            return;
        }
        SackManager.SackType sackType;
        try {
            sackType = SackManager.SackType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown sack type: " + args[1]);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("Amount must be a positive integer.");
            return;
        }
        int newCount = sackManager.addItems(player.getUniqueId(), sackType, amount);
        player.sendMessage("Added " + amount + " item(s) to " + sackType.getDisplayName()
                + ". Total: " + newCount + ".");
    }
}
