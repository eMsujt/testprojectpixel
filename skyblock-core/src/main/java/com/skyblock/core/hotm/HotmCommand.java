package com.skyblock.core.hotm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /hotm} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /hotm view [perk]}       — show level for one or all perks</li>
 *   <li>{@code /hotm upgrade <perk>}    — (op) upgrade a perk by one level</li>
 *   <li>{@code /hotm set <perk> <level>} — (op) set a perk to an exact level</li>
 *   <li>{@code /hotm reset}             — (op) reset all perks to zero</li>
 * </ul>
 * </p>
 */
public final class HotmCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "upgrade", "set", "reset");
    private static final List<String> PERK_NAMES = Arrays.stream(HotmManager.HotmPerk.values())
            .map(p -> p.name().toLowerCase())
            .collect(Collectors.toList());

    private final HotmManager hotmManager;

    public HotmCommand(HotmManager hotmManager) {
        this.hotmManager = hotmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /hotm <view|upgrade|set|reset>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"    -> handleView(player, args);
            case "upgrade" -> handleUpgrade(player, args);
            case "set"     -> handleSet(player, args);
            case "reset"   -> handleReset(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /hotm <view|upgrade|set|reset>");
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
            if (sub.equals("view") || sub.equals("upgrade") || sub.equals("set")) {
                String prefix = args[1].toLowerCase();
                return PERK_NAMES.stream()
                        .filter(p -> p.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleView(Player player, String[] args) {
        if (args.length >= 2) {
            HotmManager.HotmPerk perk = parsePerk(player, args[1]);
            if (perk == null) return;
            int level = hotmManager.getLevel(player.getUniqueId(), perk);
            player.sendMessage(formatPerk(perk) + ": " + level + "/" + perk.maxLevel);
        } else {
            player.sendMessage("=== Heart of the Mountain ===");
            for (HotmManager.HotmPerk perk : HotmManager.HotmPerk.values()) {
                int level = hotmManager.getLevel(player.getUniqueId(), perk);
                player.sendMessage(formatPerk(perk) + ": " + level + "/" + perk.maxLevel);
            }
        }
    }

    private void handleUpgrade(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /hotm upgrade <perk>");
            return;
        }
        HotmManager.HotmPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int newLevel = hotmManager.upgrade(player.getUniqueId(), perk);
        if (newLevel == -1) {
            player.sendMessage(formatPerk(perk) + " is already at max level (" + perk.maxLevel + ").");
        } else {
            player.sendMessage("Upgraded " + formatPerk(perk) + " to level " + newLevel + ".");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /hotm set <perk> <level>");
            return;
        }
        HotmManager.HotmPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int level = parseLevel(player, args[2]);
        if (level < 0) return;
        hotmManager.setLevel(player.getUniqueId(), perk, level);
        int actual = hotmManager.getLevel(player.getUniqueId(), perk);
        player.sendMessage(formatPerk(perk) + " set to " + actual + ".");
    }

    private void handleReset(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        hotmManager.reset(player.getUniqueId());
        player.sendMessage("All Heart of the Mountain perks have been reset.");
    }

    private HotmManager.HotmPerk parsePerk(Player player, String input) {
        try {
            return HotmManager.HotmPerk.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown perk: " + input + ". Valid perks: " + String.join(", ", PERK_NAMES));
            return null;
        }
    }

    private int parseLevel(Player player, String input) {
        try {
            int level = Integer.parseInt(input);
            if (level < 0) {
                player.sendMessage("Level must not be negative.");
                return -1;
            }
            return level;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid level: " + input);
            return -1;
        }
    }

    private static String formatPerk(HotmManager.HotmPerk perk) {
        String name = perk.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder(name.length());
        boolean cap = true;
        for (char c : name.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = c == ' ';
        }
        return sb.toString();
    }
}
