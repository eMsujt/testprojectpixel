package com.skyblock.core.enchanting;

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
 * Handles the {@code /enchanting} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /enchanting list}                          — list all SkyBlock enchantments</li>
 *   <li>{@code /enchanting info <enchantment>}            — show max level for an enchantment</li>
 *   <li>{@code /enchanting apply <enchantment> <level>}   — apply an enchantment at a level</li>
 *   <li>{@code /enchanting remove <enchantment>}          — remove an enchantment</li>
 *   <li>{@code /enchanting view}                          — list your active enchantments</li>
 * </ul>
 * </p>
 */
public final class EnchantingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "info", "apply", "remove", "view");

    private final EnchantingManager enchantingManager;

    public EnchantingCommand(EnchantingManager enchantingManager) {
        this.enchantingManager = enchantingManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /enchanting <list|info|apply|remove|view>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "info"   -> handleInfo(player, args);
            case "apply"  -> handleApply(player, args);
            case "remove" -> handleRemove(player, args);
            case "view"   -> handleView(player);
            default       -> player.sendMessage("Unknown subcommand. Usage: /enchanting <list|info|apply|remove|view>");
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
                return Arrays.stream(EnchantmentManager.SkyBlockEnchantment.values())
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
        Arrays.stream(EnchantmentManager.SkyBlockEnchantment.values())
                .forEach(e -> player.sendMessage(String.format(
                        "%s (max level: %d)",
                        e.name().toLowerCase().replace('_', ' '),
                        enchantingManager.getMaxLevel(e))));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting info <enchantment>");
            return;
        }
        EnchantmentManager.SkyBlockEnchantment enchantment = parseEnchantment(args[1]);
        if (enchantment == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
            return;
        }
        int currentLevel = enchantingManager.getLevel(player.getUniqueId(), enchantment);
        int maxLevel = enchantingManager.getMaxLevel(enchantment);
        player.sendMessage(String.format("%s — current level: %d / max level: %d",
                enchantment.name().toLowerCase().replace('_', ' '), currentLevel, maxLevel));
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /enchanting apply <enchantment> <level>");
            return;
        }
        EnchantmentManager.SkyBlockEnchantment enchantment = parseEnchantment(args[1]);
        if (enchantment == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
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
            enchantingManager.setEnchantment(player.getUniqueId(), enchantment, level);
            player.sendMessage("Applied " + enchantment.name().toLowerCase().replace('_', ' ')
                    + " level " + level + ".");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting remove <enchantment>");
            return;
        }
        EnchantmentManager.SkyBlockEnchantment enchantment = parseEnchantment(args[1]);
        if (enchantment == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
            return;
        }
        boolean removed = enchantingManager.removeEnchantment(player.getUniqueId(), enchantment);
        if (removed) {
            player.sendMessage("Removed " + enchantment.name().toLowerCase().replace('_', ' ') + ".");
        } else {
            player.sendMessage("You do not have " + enchantment.name().toLowerCase().replace('_', ' ') + " applied.");
        }
    }

    private void handleView(Player player) {
        Map<EnchantmentManager.SkyBlockEnchantment, Integer> enchantments =
                enchantingManager.getEnchantments(player.getUniqueId());
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

    private static EnchantmentManager.SkyBlockEnchantment parseEnchantment(String name) {
        try {
            return EnchantmentManager.SkyBlockEnchantment.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
