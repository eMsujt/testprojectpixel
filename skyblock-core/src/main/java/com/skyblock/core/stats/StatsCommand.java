package com.skyblock.core.stats;

import com.skyblock.core.stat.StatManager.StatType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class StatsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "get", "set", "reset");

    private final PlayerStatManager playerStatManager;

    public StatsCommand(PlayerStatManager playerStatManager) {
        this.playerStatManager = playerStatManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "get"   -> handleGet(player, args);
            case "set"   -> handleSet(player, args);
            case "reset" -> {
                playerStatManager.reset(player.getUniqueId());
                player.sendMessage("Stats reset.");
            }
            default -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("set"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(StatType.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Player Stats ===");
        for (StatType type : StatType.values()) {
            double value = playerStatManager.getStat(player.getUniqueId(), type);
            player.sendMessage("  " + type.name() + " : " + value);
        }
    }

    private void handleGet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /stats get <stat>");
            return;
        }
        StatType type = parseStatType(player, args[1]);
        if (type == null) {
            return;
        }
        double value = playerStatManager.getStat(player.getUniqueId(), type);
        player.sendMessage(type.name() + " : " + value);
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /stats set <stat> <value>");
            return;
        }
        StatType type = parseStatType(player, args[1]);
        if (type == null) {
            return;
        }
        double value;
        try {
            value = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number: " + args[2]);
            return;
        }
        playerStatManager.setStat(player.getUniqueId(), type, value);
        player.sendMessage(type.name() + " set to " + value + ".");
    }

    private StatType parseStatType(Player player, String name) {
        try {
            return StatType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown stat: " + name + ". Valid stats: " +
                    Arrays.stream(StatType.values()).map(Enum::name).collect(Collectors.joining(", ")));
            return null;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Stats Commands ===");
        player.sendMessage("/stats info          — show all stats");
        player.sendMessage("/stats get <stat>    — show a specific stat");
        player.sendMessage("/stats set <stat> <value> — set a stat override");
        player.sendMessage("/stats reset         — remove all stat overrides");
    }
}
