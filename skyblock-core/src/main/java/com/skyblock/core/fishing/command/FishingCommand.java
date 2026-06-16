package com.skyblock.core.fishing.command;

import com.skyblock.core.fishing.manager.FishingManager;
import com.skyblock.core.fishing.manager.TrophyFishingManager;
import com.skyblock.core.model.Rarity;

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
 * Handles the {@code /fishing} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /fishing}              — show fishing level and XP</li>
 *   <li>{@code /fishing trophy}       — show all trophy fish catch counts</li>
 *   <li>{@code /fishing trophy <fish>} — show catch count for a specific trophy fish</li>
 *   <li>{@code /fishing trophy reset}  — reset all trophy fish catches</li>
 * </ul>
 * </p>
 */
public final class FishingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("trophy", "treasure", "rarity", "fishingtrophy");
    private static final List<String> TROPHY_SUBCOMMANDS = Arrays.asList("reset");

    private final FishingManager fishingManager;
    private final TrophyFishingManager trophyFishingManager;

    public FishingCommand(FishingManager fishingManager, TrophyFishingManager trophyFishingManager) {
        if (fishingManager == null) {
            throw new IllegalArgumentException("fishingManager must not be null");
        }
        if (trophyFishingManager == null) {
            throw new IllegalArgumentException("trophyFishingManager must not be null");
        }
        this.fishingManager = fishingManager;
        this.trophyFishingManager = trophyFishingManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "trophy"         -> handleTrophy(player, args);
            case "treasure"       -> handleTreasure(player);
            case "rarity"         -> handleRarity(player);
            case "fishingtrophy"  -> handleFishingTrophy(player, args);
            default               -> sendHelp(player);
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
        if (args.length == 2 && "fishingtrophy".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            List<String> options = new java.util.ArrayList<>();
            for (FishingManager.FishingTrophy ft : FishingManager.FishingTrophy.values()) {
                options.add(ft.name().toLowerCase());
            }
            return options.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && "trophy".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            List<String> options = new java.util.ArrayList<>(TROPHY_SUBCOMMANDS);
            for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
                options.add(fish.name().toLowerCase());
            }
            return options.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        int level = fishingManager.getLevel(id);
        double xp = fishingManager.getXp(id);
        player.sendMessage("=== Fishing ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
    }

    private void handleTrophy(Player player, String[] args) {
        UUID id = player.getUniqueId();

        if (args.length == 1) {
            Map<FishingManager.TrophyFish, Integer> all = trophyFishingManager.getAllCatches(id);
            if (all.isEmpty()) {
                player.sendMessage("You have not caught any trophy fish yet.");
                return;
            }
            player.sendMessage("=== Trophy Fishing (" + all.size() + " types) ===");
            all.entrySet().stream()
               .sorted(Map.Entry.<FishingManager.TrophyFish, Integer>comparingByValue().reversed())
               .forEach(e -> player.sendMessage("  " + e.getKey().getDisplayName() + ": " + e.getValue()));
            return;
        }

        if ("reset".equalsIgnoreCase(args[1])) {
            trophyFishingManager.resetCatches(id);
            player.sendMessage("Your trophy fishing records have been reset.");
            return;
        }

        FishingManager.TrophyFish fish = parseTrophyFish(args[1]);
        if (fish == null) {
            player.sendMessage("Unknown trophy fish: " + args[1]);
            return;
        }
        int count = trophyFishingManager.getCatchCount(id, fish);
        player.sendMessage(fish.getDisplayName() + " catches: " + count);
    }

    private void handleTreasure(Player player) {
        int level = fishingManager.getLevel(player.getUniqueId());
        player.sendMessage("=== Fishing Treasures (level " + level + ") ===");
        for (FishingManager.FishingTreasure treasure : FishingManager.FishingTreasure.values()) {
            String status = level >= treasure.minLevel
                    ? String.format("%.0f%%", treasure.dropChance * 100)
                    : "Requires level " + treasure.minLevel;
            player.sendMessage("  " + treasure.displayName + ": " + status);
        }
    }

    private void handleRarity(Player player) {
        int level = fishingManager.getLevel(player.getUniqueId());
        player.sendMessage("=== Fish Rarities (level " + level + ") ===");
        for (Rarity rarity : FishingManager.FISH_RARITY_MIN_LEVEL.keySet()) {
            int minLevel = FishingManager.FISH_RARITY_MIN_LEVEL.get(rarity);
            double dropChance = FishingManager.FISH_RARITY_DROP_CHANCE.get(rarity);
            String status = level >= minLevel
                    ? String.format("%.0f%%", dropChance * 100)
                    : "Requires level " + minLevel;
            player.sendMessage("  " + rarity.getDisplayName() + ": " + status);
        }
    }

    private void handleFishingTrophy(Player player, String[] args) {
        int level = fishingManager.getLevel(player.getUniqueId());
        if (args.length == 1) {
            player.sendMessage("=== Fishing Trophies (level " + level + ") ===");
            for (FishingManager.FishingTrophy ft : FishingManager.FishingTrophy.values()) {
                String status = level >= ft.minLevel ? "Unlocked" : "Requires level " + ft.minLevel;
                player.sendMessage("  " + ft.getDisplayName() + ": " + status);
            }
            return;
        }
        FishingManager.FishingTrophy trophy = parseFishingTrophy(args[1]);
        if (trophy == null) {
            player.sendMessage("Unknown fishing trophy: " + args[1]);
            return;
        }
        String status = level >= trophy.minLevel ? "Unlocked" : "Requires level " + trophy.minLevel;
        player.sendMessage(trophy.getDisplayName() + ": " + status);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Fishing Commands ===");
        player.sendMessage("/fishing                       — show fishing level and XP");
        player.sendMessage("/fishing trophy                — show all trophy fish catches");
        player.sendMessage("/fishing trophy <fish>         — show catches for a specific trophy fish");
        player.sendMessage("/fishing trophy reset          — reset trophy fish records");
        player.sendMessage("/fishing treasure              — show fishing treasure drop chances");
        player.sendMessage("/fishing rarity                — show fish rarity drop chances");
        player.sendMessage("/fishing fishingtrophy         — show all fishing trophies and unlock status");
        player.sendMessage("/fishing fishingtrophy <name>  — show unlock status for a specific trophy");
    }

    private static FishingManager.FishingTrophy parseFishingTrophy(String name) {
        for (FishingManager.FishingTrophy ft : FishingManager.FishingTrophy.values()) {
            if (ft.name().equalsIgnoreCase(name)) {
                return ft;
            }
        }
        return null;
    }

    private static FishingManager.TrophyFish parseTrophyFish(String name) {
        for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
            if (fish.name().equalsIgnoreCase(name)) {
                return fish;
            }
        }
        return null;
    }
}
