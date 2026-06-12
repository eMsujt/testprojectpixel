package com.skyblock.core.reward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles the {@code /dailyreward} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /dailyreward}        — claim your daily reward (1,000 coins)</li>
 *   <li>{@code /dailyreward status} — show time remaining until next claim</li>
 * </ul>
 * </p>
 */
public final class DailyRewardCommand implements TabExecutor {

    private final DailyRewardManager manager;

    public DailyRewardCommand(DailyRewardManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("status")) {
            handleStatus(player);
        } else {
            handleClaim(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("status").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleClaim(Player player) {
        try {
            manager.claim(player.getUniqueId());
            player.sendMessage("You claimed your daily reward: 1,000 coins!");
        } catch (IllegalStateException e) {
            long ms = manager.millisUntilClaim(player.getUniqueId());
            player.sendMessage("You already claimed your daily reward. Come back in " + formatTime(ms) + ".");
        }
    }

    private void handleStatus(Player player) {
        long ms = manager.millisUntilClaim(player.getUniqueId());
        if (ms == 0) {
            player.sendMessage("Your daily reward is ready to claim! Use /dailyreward to collect it.");
        } else {
            player.sendMessage("Next daily reward in: " + formatTime(ms));
        }
    }

    private static String formatTime(long ms) {
        long hours = TimeUnit.MILLISECONDS.toHours(ms);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
