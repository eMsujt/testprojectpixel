package com.skyblock.core.sack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SacksCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "view", "type");

    private final SacksManager sacksManager;

    public SacksCommand(SacksManager sacksManager) {
        this.sacksManager = sacksManager;
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
            case "info" -> handleInfo(player);
            case "view" -> handleView(player, args);
            case "type" -> handleType(player, args);
            default     -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("type"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(SacksManager.SackType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Sacks ===");
        for (SacksManager.SackType type : SacksManager.SackType.values()) {
            int count = sacksManager.getSackContents(player.getUniqueId(), type).values()
                    .stream().mapToInt(Integer::intValue).sum();
            player.sendMessage("  " + type.getDisplayName() + ": " + count + " items");
        }
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /sacks view <type>");
            return;
        }
        SacksManager.SackType type;
        try {
            type = SacksManager.SackType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown sack type: " + args[1]);
            return;
        }
        Map<String, Integer> contents = sacksManager.getSackContents(player.getUniqueId(), type);
        if (contents.isEmpty()) {
            player.sendMessage(type.getDisplayName() + " Sack is empty.");
            return;
        }
        player.sendMessage("=== " + type.getDisplayName() + " Sack ===");
        for (Map.Entry<String, Integer> entry : contents.entrySet()) {
            player.sendMessage("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private void handleType(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("=== Sack Types ===");
            for (SacksManager.SackType type : SacksManager.SackType.values()) {
                player.sendMessage("  " + type.name() + " — " + type.getDisplayName());
            }
            return;
        }
        SacksManager.SackType type;
        try {
            type = SacksManager.SackType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown sack type: " + args[1]);
            return;
        }
        int total = sacksManager.getSackContents(player.getUniqueId(), type).values()
                .stream().mapToInt(Integer::intValue).sum();
        player.sendMessage(type.getDisplayName() + " Sack: " + total + " items");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Sacks Commands ===");
        player.sendMessage("/sacks info          — show all sack summaries");
        player.sendMessage("/sacks view <type>   — show contents of a sack");
        player.sendMessage("/sacks type [type]   — list types or show one");
    }
}
