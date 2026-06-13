package com.skyblock.core.enchantment;

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
 * Handles the {@code /skyblockenchant} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /skyblockenchant list}                       — list all SkyBlock enchants</li>
 *   <li>{@code /skyblockenchant info <enchant>}              — show max level for an enchant</li>
 *   <li>{@code /skyblockenchant apply <enchant> <level>}     — apply an enchant at a level</li>
 *   <li>{@code /skyblockenchant remove <enchant>}            — remove an enchant</li>
 *   <li>{@code /skyblockenchant view}                        — list your active enchants</li>
 * </ul>
 * </p>
 */
public final class EnchantCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "info", "apply", "remove", "view");

    private final SkyBlockEnchantManager enchantManager;

    public EnchantCommand(SkyBlockEnchantManager enchantManager) {
        this.enchantManager = enchantManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /skyblockenchant <list|info|apply|remove|view>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "info"   -> handleInfo(player, args);
            case "apply"  -> handleApply(player, args);
            case "remove" -> handleRemove(player, args);
            case "view"   -> handleView(player);
            default       -> player.sendMessage(
                    "Unknown subcommand. Usage: /skyblockenchant <list|info|apply|remove|view>");
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
                return Arrays.stream(SkyBlockEnchantManager.SkyBlockEnchant.values())
                        .map(e -> e.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Enchants ===");
        Arrays.stream(SkyBlockEnchantManager.SkyBlockEnchant.values())
                .forEach(e -> player.sendMessage(String.format(
                        "%s (max level: %d)",
                        e.name().toLowerCase().replace('_', ' '),
                        enchantManager.getMaxLevel(e))));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblockenchant info <enchant>");
            return;
        }
        SkyBlockEnchantManager.SkyBlockEnchant enchant = parseEnchant(args[1]);
        if (enchant == null) {
            player.sendMessage("Unknown enchant: " + args[1]
                    + ". Use /skyblockenchant list to see available enchants.");
            return;
        }
        int currentLevel = enchantManager.getLevel(player.getUniqueId(), enchant);
        int maxLevel = enchantManager.getMaxLevel(enchant);
        player.sendMessage(String.format("%s — current level: %d / max level: %d",
                enchant.name().toLowerCase().replace('_', ' '), currentLevel, maxLevel));
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /skyblockenchant apply <enchant> <level>");
            return;
        }
        SkyBlockEnchantManager.SkyBlockEnchant enchant = parseEnchant(args[1]);
        if (enchant == null) {
            player.sendMessage("Unknown enchant: " + args[1]
                    + ". Use /skyblockenchant list to see available enchants.");
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
            enchantManager.setEnchant(player.getUniqueId(), enchant, level);
            player.sendMessage("Applied " + enchant.name().toLowerCase().replace('_', ' ')
                    + " level " + level + ".");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblockenchant remove <enchant>");
            return;
        }
        SkyBlockEnchantManager.SkyBlockEnchant enchant = parseEnchant(args[1]);
        if (enchant == null) {
            player.sendMessage("Unknown enchant: " + args[1]
                    + ". Use /skyblockenchant list to see available enchants.");
            return;
        }
        boolean removed = enchantManager.removeEnchant(player.getUniqueId(), enchant);
        if (removed) {
            player.sendMessage("Removed " + enchant.name().toLowerCase().replace('_', ' ') + ".");
        } else {
            player.sendMessage("You do not have "
                    + enchant.name().toLowerCase().replace('_', ' ') + " applied.");
        }
    }

    private void handleView(Player player) {
        Map<SkyBlockEnchantManager.SkyBlockEnchant, Integer> enchants =
                enchantManager.getEnchants(player.getUniqueId());
        if (enchants.isEmpty()) {
            player.sendMessage("You have no active enchants.");
            return;
        }
        player.sendMessage("=== Your Enchants ===");
        enchants.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(
                        e.getKey().name().toLowerCase().replace('_', ' ') + " " + e.getValue()));
    }

    private static SkyBlockEnchantManager.SkyBlockEnchant parseEnchant(String name) {
        try {
            return SkyBlockEnchantManager.SkyBlockEnchant.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
