package com.skyblock.core.reward;

import com.skyblock.core.economy.EconomyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles the {@code /dailyreward} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /dailyreward}        — claim today's reward (1 000 coins) if eligible</li>
 *   <li>{@code /dailyreward status} — show time remaining until next claim</li>
 * </ul>
 * </p>
 */
public final class DailyRewardCommand implements TabExecutor {

    private final DailyRewardManager rewardManager;
    private final EconomyManager economyManager;

    public DailyRewardCommand(DailyRewardManager rewardManager, EconomyManager economyManager) {
        this.rewardManager = rewardManager;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("status")) {
            sendStatus(player);
            return true;
        }

        if (!rewardManager.canClaim(player.getUniqueId())) {
            long remaining = rewardManager.getRemainingCooldownMs(player.getUniqueId());
            player.sendMessage("You have already claimed your daily reward. Next claim in: "
                    + formatDuration(remaining) + ".");
            return true;
        }

        rewardManager.recordClaim(player.getUniqueId());
        economyManager.deposit(player.getUniqueId(), DailyRewardManager.REWARD_COINS);
        player.sendMessage("You claimed your daily reward: +"
                + DailyRewardManager.REWARD_COINS + " coins!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "status".startsWith(args[0].toLowerCase())) {
            return List.of("status");
        }
        return Collections.emptyList();
    }

    private void sendStatus(Player player) {
        if (rewardManager.canClaim(player.getUniqueId())) {
            player.sendMessage("Your daily reward is ready to claim! Use /dailyreward to collect it.");
        } else {
            long remaining = rewardManager.getRemainingCooldownMs(player.getUniqueId());
            player.sendMessage("Next daily reward in: " + formatDuration(remaining) + ".");
        }
    }

    private static String formatDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
