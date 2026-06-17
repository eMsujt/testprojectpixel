package com.skyblock.core.manager;

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
 * Handles the {@code /trophyfish} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /trophyfish}        — show all trophy fish catch counts, tiers, and total points</li>
 *   <li>{@code /trophyfish info}   — same as above</li>
 *   <li>{@code /trophyfish reset}  — reset all trophy fish catch counts</li>
 * </ul>
 * </p>
 */
public final class TrophyFishCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "reset");

    private final TrophyFishManager trophyFishManager;

    public TrophyFishCommand(TrophyFishManager trophyFishManager) {
        this.trophyFishManager = trophyFishManager;
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
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        UUID id = player.getUniqueId();
        Map<FishingManager.TrophyFish, Integer> catches = trophyFishManager.getAllCatches(id);
        if (catches.isEmpty()) {
            player.sendMessage("You have not caught any trophy fish yet. Go fishing in the Crimson Isle!");
            return;
        }
        player.sendMessage("=== Your Trophy Fish (" + catches.size() + " types, "
                + trophyFishManager.getTotalPoints(id) + " points) ===");
        catches.entrySet().stream()
                .sorted(Map.Entry.<FishingManager.TrophyFish, Integer>comparingByValue().reversed())
                .forEach(e -> {
                    TrophyFishManager.TrophyTier tier = trophyFishManager.getTier(id, e.getKey());
                    player.sendMessage("  " + e.getKey().getDisplayName() + ": " + e.getValue()
                            + " (" + tier.name() + ")");
                });
    }

    private void handleReset(Player player) {
        trophyFishManager.resetCatches(player.getUniqueId());
        player.sendMessage("Your trophy fish catches have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trophy Fish Commands ===");
        player.sendMessage("/trophyfish        — show all catch counts and tiers");
        player.sendMessage("/trophyfish info   — show all catch counts and tiers");
        player.sendMessage("/trophyfish reset  — reset all catch counts");
    }
}
