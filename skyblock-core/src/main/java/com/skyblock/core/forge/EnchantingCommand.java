package com.skyblock.core.forge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /enchanting} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /enchanting}                     — show all enchantments</li>
 *   <li>{@code /enchanting view <enchant>}       — show level for a specific enchantment</li>
 *   <li>{@code /enchanting set <enchant> <level>} — set an enchantment level</li>
 *   <li>{@code /enchanting reset}               — reset all enchantments</li>
 * </ul>
 * </p>
 */
public final class EnchantingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "set", "reset");

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
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"  -> handleView(player, args);
            case "set"   -> handleSet(player, args);
            case "reset" -> handleReset(player);
            default      -> sendHelp(player);
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
            if ("view".equals(sub) || "set".equals(sub)) {
                String prefix = args[1].toLowerCase();
                return enchantingManager.getAllEnchantments(player.getUniqueId()).keySet().stream()
                        .filter(e -> e.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Integer> all = enchantingManager.getAllEnchantments(id);
        if (all.isEmpty()) {
            player.sendMessage("You have no enchantments recorded.");
            return;
        }
        player.sendMessage("=== Your Enchantments (" + all.size() + ") ===");
        all.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage("  " + e.getKey() + ": " + e.getValue()));
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting view <enchant>");
            return;
        }
        String enchant = args[1];
        int level = enchantingManager.getEnchantmentLevel(player.getUniqueId(), enchant);
        player.sendMessage(enchant + " level: " + level);
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /enchanting set <enchant> <level>");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        if (level < 0) {
            player.sendMessage("Level must be 0 or greater.");
            return;
        }
        enchantingManager.setEnchantment(player.getUniqueId(), args[1], level);
        player.sendMessage("Set " + args[1] + " to level " + level + ".");
    }

    private void handleReset(Player player) {
        enchantingManager.resetEnchantments(player.getUniqueId());
        player.sendMessage("Your enchantments have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Enchanting Commands ===");
        player.sendMessage("/enchanting                        — show all enchantments");
        player.sendMessage("/enchanting view <enchant>         — show level for a specific enchantment");
        player.sendMessage("/enchanting set <enchant> <level>  — set an enchantment level");
        player.sendMessage("/enchanting reset                  — reset all enchantments");
    }
}
