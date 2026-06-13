package com.skyblock.core.fishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
 *   <li>{@code /trophyfish}            — list all trophy fish catch counts</li>
 *   <li>{@code /trophyfish info <fish>} — show catch count for a specific trophy fish</li>
 *   <li>{@code /trophyfish reset}      — reset all trophy fish catches</li>
 * </ul>
 * </p>
 */
public final class TrophyFishCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "reset");

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
            handleList(player);
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
            List<String> options = new ArrayList<>();
            for (TrophyFishManager.TrophyFish fish : TrophyFishManager.TrophyFish.values()) {
                options.add(fish.name().toLowerCase());
            }
            return options.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        Map<TrophyFishManager.TrophyFish, Integer> all = trophyFishManager.getCatches(id);
        if (all.isEmpty()) {
            player.sendMessage("You have not caught any trophy fish yet.");
            return;
        }
        player.sendMessage("=== Trophy Fish (" + all.size() + " types) ===");
        all.entrySet().stream()
           .sorted(Map.Entry.<TrophyFishManager.TrophyFish, Integer>comparingByValue().reversed())
           .forEach(e -> player.sendMessage("  " + e.getKey().getDisplayName() + ": " + e.getValue()));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trophyfish info <fish>");
            return;
        }
        TrophyFishManager.TrophyFish fish = parseTrophyFish(args[1]);
        if (fish == null) {
            player.sendMessage("Unknown trophy fish: " + args[1]);
            return;
        }
        int count = trophyFishManager.getCatchCount(player.getUniqueId(), fish);
        player.sendMessage(fish.getDisplayName() + " catches: " + count + "  (min level: " + fish.minLevel + ")");
    }

    private void handleReset(Player player) {
        trophyFishManager.remove(player.getUniqueId());
        player.sendMessage("Your trophy fish records have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trophy Fish Commands ===");
        player.sendMessage("/trophyfish               — list all trophy fish catches");
        player.sendMessage("/trophyfish info <fish>   — show catches for a specific trophy fish");
        player.sendMessage("/trophyfish reset         — reset all trophy fish records");
    }

    private static TrophyFishManager.TrophyFish parseTrophyFish(String name) {
        for (TrophyFishManager.TrophyFish fish : TrophyFishManager.TrophyFish.values()) {
            if (fish.name().equalsIgnoreCase(name)) {
                return fish;
            }
        }
        return null;
    }
}
