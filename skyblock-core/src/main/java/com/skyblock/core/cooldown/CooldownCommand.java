package com.skyblock.core.cooldown;

import org.bukkit.Bukkit;
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
 * <p>Subcommands (all require {@code skyblock.cooldown}, default: op):
 * <ul>
 *   <li>{@code /cooldown list [player]}         — list active cooldowns</li>
 *   <li>{@code /cooldown set <player> <key> <seconds>} — start a cooldown</li>
 *   <li>{@code /cooldown clear <player> <key>}  — clear one cooldown</li>
 *   <li>{@code /cooldown clearall <player>}      — clear all cooldowns</li>
 *   <li>{@code /cooldown check <player> <key>}  — check remaining time</li>
 * </ul>
 * </p>
 */
public final class CooldownCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "set", "clear", "clearall", "check");
    private static final String PERMISSION = "skyblock.cooldown";

    private final CooldownManager cooldownManager;

    public CooldownCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(sender, args);
            case "set"      -> handleSet(sender, args);
            case "clear"    -> handleClear(sender, args);
            case "clearall" -> handleClearAll(sender, args);
            case "check"    -> handleCheck(sender, args);
            default         -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION)) return Collections.emptyList();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(CommandSender sender, String[] args) {
        Player target = resolvePlayer(sender, args.length >= 2 ? args[1] : null);
        if (target == null) return;
        Map<String, Long> cds = cooldownManager.getCooldowns(target.getUniqueId());
        if (cds.isEmpty()) {
            sender.sendMessage(target.getName() + " has no active cooldowns.");
            return;
        }
        long now = System.currentTimeMillis();
        sender.sendMessage("=== Cooldowns for " + target.getName() + " ===");
        cds.forEach((key, expiry) -> {
            long remainSec = Math.max(0L, (expiry - now) / 1000L);
            sender.sendMessage("  " + key + ": " + remainSec + "s remaining");
        });
    }

    private void handleSet(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Usage: /cooldown set <player> <key> <seconds>");
            return;
        }
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        String key = args[2];
        long seconds;
        try {
            seconds = Long.parseLong(args[3]);
            if (seconds <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage("Seconds must be a positive integer.");
            return;
        }
        cooldownManager.setCooldown(target.getUniqueId(), key, seconds * 1000L);
        sender.sendMessage("Set cooldown '" + key + "' for " + target.getName() + " (" + seconds + "s).");
    }

    private void handleClear(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /cooldown clear <player> <key>");
            return;
        }
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        String key = args[2];
        cooldownManager.clearCooldown(target.getUniqueId(), key);
        sender.sendMessage("Cleared cooldown '" + key + "' for " + target.getName() + ".");
    }

    private void handleClearAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Usage: /cooldown clearall <player>");
            return;
        }
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        cooldownManager.clearAll(target.getUniqueId());
        sender.sendMessage("Cleared all cooldowns for " + target.getName() + ".");
    }

    private void handleCheck(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /cooldown check <player> <key>");
            return;
        }
        Player target = resolvePlayer(sender, args[1]);
        if (target == null) return;
        String key = args[2];
        long remainMs = cooldownManager.getRemainingMs(target.getUniqueId(), key);
        if (remainMs <= 0) {
            sender.sendMessage(target.getName() + " has no active cooldown for '" + key + "'.");
        } else {
            sender.sendMessage(target.getName() + " has " + (remainMs / 1000L) + "s remaining on '" + key + "'.");
        }
    }

    private Player resolvePlayer(CommandSender sender, String name) {
        if (name == null) {
            if (sender instanceof Player p) return p;
            sender.sendMessage("Console must specify a player name.");
            return null;
        }
        Player p = Bukkit.getPlayerExact(name);
        if (p == null) {
            sender.sendMessage("Player '" + name + "' is not online.");
        }
        return p;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("=== Cooldown Commands ===");
        sender.sendMessage("/cooldown list [player]             — list active cooldowns");
        sender.sendMessage("/cooldown set <player> <key> <sec>  — start a cooldown");
        sender.sendMessage("/cooldown clear <player> <key>      — clear one cooldown");
        sender.sendMessage("/cooldown clearall <player>         — clear all cooldowns");
        sender.sendMessage("/cooldown check <player> <key>      — check remaining time");
    }
}
