package com.skyblock.core.slayer;

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
 * Handles the {@code /slay} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /slay}              — lists all slayer levels for the sender</li>
 *   <li>{@code /slay info <type>}  — shows XP details for a specific slayer type</li>
 *   <li>{@code /slay start <type>} — starts a slayer quest for the given type</li>
 * </ul>
 * </p>
 */
public final class SlayerCommand implements TabExecutor {

    private final SlayerManager slayerManager;

    public SlayerCommand(SlayerManager slayerManager) {
        this.slayerManager = slayerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendSlayerList(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("info") || sub.equals("start")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /slay " + sub + " <" + typeList() + ">");
                return true;
            }
            SlayerManager.SlayerType type = parseType(player, args[1]);
            if (type == null) return true;
            if (sub.equals("start")) {
                sendQuestStart(player, type);
            } else {
                sendSlayerDetail(player, type);
            }
            return true;
        }

        // treat the first arg as a type name (shorthand: /slay revenant)
        SlayerManager.SlayerType type = parseType(player, args[0]);
        if (type != null) {
            sendSlayerDetail(player, type);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = Arrays.asList("info", "start");
            Arrays.stream(SlayerManager.SlayerType.values())
                    .map(t -> t.name().toLowerCase())
                    .forEach(options::add);
            return options.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("start"))) {
            return Arrays.stream(SlayerManager.SlayerType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendSlayerList(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Your Slayers ===");
        for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
            int level = slayerManager.getLevel(id, type);
            long xp = slayerManager.getXp(id, type);
            int kills = slayerManager.getKills(id, type);
            player.sendMessage(capitalize(type.name()) + ": Level " + level
                    + "/" + SlayerManager.MAX_LEVEL + " | " + xp + " XP | " + kills + " kills");
        }
    }

    private void sendSlayerDetail(Player player, SlayerManager.SlayerType type) {
        UUID id = player.getUniqueId();
        int level = slayerManager.getLevel(id, type);
        long xp = slayerManager.getXp(id, type);
        int kills = slayerManager.getKills(id, type);
        long toNext = slayerManager.xpToNextLevel(id, type);
        player.sendMessage("=== " + capitalize(type.name()) + " Slayer ===");
        player.sendMessage("Level: " + level + "/" + SlayerManager.MAX_LEVEL);
        player.sendMessage("Total XP: " + xp);
        player.sendMessage("Boss Kills: " + kills);
        if (toNext > 0) {
            player.sendMessage("XP to next level: " + toNext);
        } else {
            player.sendMessage("Max level reached!");
        }
    }

    private void sendQuestStart(Player player, SlayerManager.SlayerType type) {
        player.sendMessage("Starting " + capitalize(type.name()) + " Slayer quest! Defeat the required mobs to spawn the boss.");
    }

    private SlayerManager.SlayerType parseType(Player player, String input) {
        try {
            return SlayerManager.SlayerType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slayer type: " + input + ". Options: " + typeList());
            return null;
        }
    }

    private static String typeList() {
        return Arrays.stream(SlayerManager.SlayerType.values())
                .map(t -> t.name().toLowerCase())
                .collect(Collectors.joining("|"));
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
