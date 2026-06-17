package com.skyblock.core.stat;

import com.skyblock.core.manager.StatManager;
import com.skyblock.core.model.Stat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /stat} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /stat}           — list all your stats</li>
 *   <li>{@code /stat <stat>}    — show the effective value for one stat</li>
 * </ul>
 * </p>
 */
public final class StatCommand implements TabExecutor {

    private static final List<String> STAT_NAMES = Arrays.stream(Stat.values())
            .map(s -> s.name().toLowerCase())
            .collect(Collectors.toList());

    private final StatManager statManager;

    public StatCommand(StatManager statManager) {
        this.statManager = statManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("--- Your Stats ---");
            for (Stat type : Stat.values()) {
                double value = statManager.getStat(player.getUniqueId(), type);
                player.sendMessage(formatName(type) + ": " + value);
            }
            return true;
        }

        String input = args[0].toUpperCase();
        Stat type;
        try {
            type = Stat.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown stat '" + args[0] + "'. Options: " + STAT_NAMES);
            return true;
        }

        double value = statManager.getStat(player.getUniqueId(), type);
        player.sendMessage(formatName(type) + ": " + value);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return STAT_NAMES.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static String formatName(Stat type) {
        String raw = type.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder(raw.length());
        boolean cap = true;
        for (char c : raw.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = c == ' ';
        }
        return sb.toString();
    }
}
