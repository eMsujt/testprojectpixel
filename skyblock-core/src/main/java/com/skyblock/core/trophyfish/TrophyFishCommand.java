package com.skyblock.core.trophyfish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /trophyfish} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /trophyfish}              — show all trophy fish catch counts</li>
 *   <li>{@code /trophyfish info}         — show all trophy fish with drop chances</li>
 *   <li>{@code /trophyfish catches}      — show caught fish totals</li>
 *   <li>{@code /trophyfish fish <name>}  — show catch count for a specific trophy fish</li>
 *   <li>{@code /trophyfish reset}        — reset all trophy fish catches</li>
 * </ul>
 * </p>
 */
public final class TrophyFishCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "catches", "fish", "reset");

    private final TrophyFishManager trophyFishManager;

    public TrophyFishCommand(TrophyFishManager trophyFishManager) {
        if (trophyFishManager == null) {
            throw new IllegalArgumentException("trophyFishManager must not be null");
        }
        this.trophyFishManager = trophyFishManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleCatches(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"    -> handleInfo(player);
            case "catches" -> handleCatches(player);
            case "fish"    -> handleFish(player, args);
            case "reset"   -> handleReset(player);
            default        -> sendHelp(player);
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
        if (args.length == 2 && "fish".equalsIgnoreCase(args[0])) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(TrophyFishManager.TrophyFish.values())
                    .map(f -> f.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Trophy Fish ===");
        for (TrophyFishManager.TrophyFish fish : TrophyFishManager.TrophyFish.values()) {
            player.sendMessage(String.format("  %s (lvl %d): %.0f%%",
                    fish.getDisplayName(), fish.minLevel, fish.dropChance * 100));
        }
    }

    private void handleCatches(Player player) {
        Map<TrophyFishManager.TrophyFish, Integer> all = trophyFishManager.getCatches(player.getUniqueId());
        if (all.isEmpty()) {
            player.sendMessage("You have not caught any trophy fish yet.");
            return;
        }
        player.sendMessage("=== Trophy Fish Catches (" + all.size() + " types) ===");
        all.entrySet().stream()
           .sorted(Map.Entry.<TrophyFishManager.TrophyFish, Integer>comparingByValue().reversed())
           .forEach(e -> player.sendMessage("  " + e.getKey().getDisplayName() + ": " + e.getValue()));
    }

    private void handleFish(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trophyfish fish <name>");
            return;
        }
        TrophyFishManager.TrophyFish fish;
        try {
            fish = TrophyFishManager.TrophyFish.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown trophy fish: " + args[1]);
            return;
        }
        int count = trophyFishManager.getCatchCount(player.getUniqueId(), fish);
        player.sendMessage(fish.getDisplayName() + " catches: " + count);
    }

    private void handleReset(Player player) {
        trophyFishManager.remove(player.getUniqueId());
        player.sendMessage("Your trophy fish records have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trophy Fish Commands ===");
        player.sendMessage("/trophyfish               — show all catch counts");
        player.sendMessage("/trophyfish info          — show all trophy fish and drop chances");
        player.sendMessage("/trophyfish catches       — show caught fish totals");
        player.sendMessage("/trophyfish fish <name>   — show catches for a specific trophy fish");
        player.sendMessage("/trophyfish reset         — reset all trophy fish records");
    }
}
