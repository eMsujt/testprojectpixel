package com.skyblock.core.enchant;

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
 * Handles the {@code /enchantment} command.
 *
 * <p>Subcommands: list, info, apply, remove, view.</p>
 */
public final class EnchantmentCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "info", "apply", "remove", "view");

    private final EnchantmentManager enchantmentManager;

    public EnchantmentCommand(EnchantmentManager enchantmentManager) {
        this.enchantmentManager = enchantmentManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /enchantment <list|info|apply|remove|view>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "info"   -> handleInfo(player, args);
            case "apply"  -> handleApply(player, args);
            case "remove" -> handleRemove(player, args);
            case "view"   -> handleView(player);
            default       -> player.sendMessage("Unknown subcommand. Usage: /enchantment <list|info|apply|remove|view>");
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("apply") || sub.equals("remove")) {
                String prefix = args[1].toLowerCase();
                return Arrays.stream(EnchantManager.EnchantType.values())
                        .map(e -> e.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Enchantments ===");
        Arrays.stream(EnchantManager.EnchantType.values())
                .forEach(e -> player.sendMessage(String.format(
                        "%s (max level: %d)",
                        e.name().toLowerCase().replace('_', ' '),
                        enchantmentManager.getMaxLevel(e))));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchantment info <enchantment>");
            return;
        }
        EnchantManager.EnchantType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchantment list to see available enchantments.");
            return;
        }
        int currentLevel = enchantmentManager.getLevel(player.getUniqueId(), type);
        int maxLevel = enchantmentManager.getMaxLevel(type);
        player.sendMessage(String.format("%s — current level: %d / max level: %d",
                type.name().toLowerCase().replace('_', ' '), currentLevel, maxLevel));
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /enchantment apply <enchantment> <level>");
            return;
        }
        EnchantManager.EnchantType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchantment list to see available enchantments.");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        try {
            enchantmentManager.setEnchantment(player.getUniqueId(), type, level);
            player.sendMessage("Applied " + type.name().toLowerCase().replace('_', ' ')
                    + " level " + level + ".");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchantment remove <enchantment>");
            return;
        }
        EnchantManager.EnchantType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchantment list to see available enchantments.");
            return;
        }
        boolean removed = enchantmentManager.removeEnchantment(player.getUniqueId(), type);
        if (removed) {
            player.sendMessage("Removed " + type.name().toLowerCase().replace('_', ' ') + ".");
        } else {
            player.sendMessage("You do not have " + type.name().toLowerCase().replace('_', ' ') + " applied.");
        }
    }

    private void handleView(Player player) {
        Map<EnchantManager.EnchantType, Integer> enchantments =
                enchantmentManager.getEnchantments(player.getUniqueId());
        if (enchantments.isEmpty()) {
            player.sendMessage("You have no active enchantments.");
            return;
        }
        player.sendMessage("=== Your Enchantments ===");
        enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(
                        e.getKey().name().toLowerCase().replace('_', ' ') + " " + e.getValue()));
    }

    private static EnchantManager.EnchantType parseType(String name) {
        try {
            return EnchantManager.EnchantType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
