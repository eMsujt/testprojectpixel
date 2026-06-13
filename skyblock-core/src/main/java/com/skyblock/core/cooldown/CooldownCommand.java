package com.skyblock.core.cooldown;

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
 * Handles the {@code /cooldown} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /cooldown list}                — list all active cooldowns for yourself</li>
 *   <li>{@code /cooldown check <key>}          — show remaining time for one key</li>
 *   <li>{@code /cooldown set <key> <seconds>}  — (op) start a cooldown for yourself</li>
 *   <li>{@code /cooldown clear <key>}          — (op) clear a specific cooldown</li>
 *   <li>{@code /cooldown clearall}             — (op) clear all your cooldowns</li>
 * </ul>
 * </p>
 */
public final class CooldownCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "check", "set", "clear", "clearall");

    private final CooldownManager cooldownManager;

    public CooldownCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /cooldown <list|check|set|clear|clearall>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "check"   -> handleCheck(player, args);
            case "set"     -> handleSet(player, args);
            case "clear"   -> handleClear(player, args);
            case "clearall" -> handleClearAll(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /cooldown <list|check|set|clear|clearall>");
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
            if (sub.equals("check") || sub.equals("clear")) {
                String prefix = args[1].toLowerCase();
                return cooldownManager.getCooldowns(player.getUniqueId()).keySet().stream()
                        .filter(k -> k.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        Map<String, Long> all = cooldownManager.getCooldowns(player.getUniqueId());
        long now = System.currentTimeMillis();
        List<Map.Entry<String, Long>> active = all.entrySet().stream()
                .filter(e -> e.getValue() > now)
                .collect(Collectors.toList());
        if (active.isEmpty()) {
            player.sendMessage("You have no active cooldowns.");
            return;
        }
        player.sendMessage("=== Active Cooldowns ===");
        for (Map.Entry<String, Long> entry : active) {
            long remainingSec = (entry.getValue() - now) / 1000L;
            player.sendMessage(entry.getKey() + ": " + remainingSec + "s remaining");
        }
    }

    private void handleCheck(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /cooldown check <key>");
            return;
        }
        String key = args[1];
        long remainingMs = cooldownManager.getRemainingMs(player.getUniqueId(), key);
        if (remainingMs <= 0) {
            player.sendMessage("No active cooldown for '" + key + "'.");
        } else {
            player.sendMessage("'" + key + "' has " + (remainingMs / 1000L) + "s remaining.");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /cooldown set <key> <seconds>");
            return;
        }
        String key = args[1];
        long seconds;
        try {
            seconds = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid duration: " + args[2]);
            return;
        }
        if (seconds <= 0) {
            player.sendMessage("Duration must be a positive number of seconds.");
            return;
        }
        cooldownManager.setCooldown(player.getUniqueId(), key, seconds * 1000L);
        player.sendMessage("Cooldown '" + key + "' set for " + seconds + "s.");
    }

    private void handleClear(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /cooldown clear <key>");
            return;
        }
        String key = args[1];
        boolean removed = cooldownManager.clearCooldown(player.getUniqueId(), key);
        if (removed) {
            player.sendMessage("Cooldown '" + key + "' cleared.");
        } else {
            player.sendMessage("No cooldown found for '" + key + "'.");
        }
    }

    private void handleClearAll(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        cooldownManager.clearAll(player.getUniqueId());
        player.sendMessage("All your cooldowns have been cleared.");
    }
}
