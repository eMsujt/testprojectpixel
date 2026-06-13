package com.skyblock.core.foraging;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /foraging} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /foraging}              — show foraging level and XP</li>
 *   <li>{@code /foraging info <log>}   — show chop count for a specific log type</li>
 *   <li>{@code /foraging reset}        — reset all foraging progression</li>
 * </ul>
 * </p>
 */
public final class ForagingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "reset");

    private final ForagingManager foragingManager;

    public ForagingCommand(ForagingManager foragingManager) {
        if (foragingManager == null) {
            throw new IllegalArgumentException("foragingManager must not be null");
        }
        this.foragingManager = foragingManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player, args);
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
        if (args.length == 2 && "info".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(ForagingManager.LogType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        int level = foragingManager.getLevel(id);
        double xp = foragingManager.getXp(id);
        double speed = foragingManager.getSpeedMultiplierForPlayer(id);
        player.sendMessage("=== Foraging ===");
        player.sendMessage("  Level: " + level + "  XP: " + String.format("%.1f", xp));
        player.sendMessage("  Speed Multiplier: " + String.format("%.2f", speed) + "x");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /foraging info <log>");
            return;
        }
        ForagingManager.LogType logType = parseLogType(args[1]);
        if (logType == null) {
            player.sendMessage("Unknown log type: " + args[1]);
            return;
        }
        ForagingManager.TreeType tree = matchTreeType(logType);
        int chops = tree != null ? foragingManager.getChops(player.getUniqueId(), tree) : 0;
        player.sendMessage(logType.getDisplayName() + " chops: " + chops);
    }

    private void handleReset(Player player) {
        foragingManager.reset(player.getUniqueId());
        player.sendMessage("Your foraging progression has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Foraging Commands ===");
        player.sendMessage("/foraging              — show your foraging level and XP");
        player.sendMessage("/foraging info <log>   — show chop count for a log type");
        player.sendMessage("/foraging reset        — reset all foraging progression");
    }

    private static ForagingManager.LogType parseLogType(String name) {
        for (ForagingManager.LogType type : ForagingManager.LogType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    private static ForagingManager.TreeType matchTreeType(ForagingManager.LogType logType) {
        try {
            return ForagingManager.TreeType.valueOf(logType.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
