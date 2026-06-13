package com.skyblock.core.trophy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /trophy} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /trophy list}             — list all available trophies</li>
 *   <li>{@code /trophy unlocked}         — list trophies the player has unlocked</li>
 *   <li>{@code /trophy info <trophy>}    — show description for a trophy</li>
 *   <li>{@code /trophy unlock <trophy>}  — unlock a trophy for the player</li>
 *   <li>{@code /trophy reset}            — reset all trophies for the player</li>
 * </ul>
 * </p>
 */
public final class TrophyCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "unlocked", "info", "unlock", "reset");

    private final TrophyManager trophyManager;

    public TrophyCommand(TrophyManager trophyManager) {
        this.trophyManager = trophyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /trophy <list|unlocked|info|unlock|reset>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(player);
            case "unlocked" -> handleUnlocked(player);
            case "info"     -> handleInfo(player, args);
            case "unlock"   -> handleUnlock(player, args);
            case "reset"    -> handleReset(player);
            default         -> player.sendMessage("Unknown subcommand. Usage: /trophy <list|unlocked|info|unlock|reset>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("unlock"))) {
            String prefix = args[1].toLowerCase();
            return trophyManager.getTrophies().keySet().stream()
                    .filter(id -> id.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Trophies (" + trophyManager.getTrophies().size() + " total) ===");
        trophyManager.getTrophies().values().stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage(String.format("[%s] %s — %s",
                        t.name().toLowerCase(), t.getDisplayName(), t.getDescription())));
    }

    private void handleUnlocked(Player player) {
        java.util.Set<TrophyManager.TrophyType> unlocked =
                trophyManager.getUnlockedTrophies(player.getUniqueId());
        if (unlocked.isEmpty()) {
            player.sendMessage("You have not unlocked any trophies yet.");
            return;
        }
        player.sendMessage("=== Your Trophies (" + unlocked.size() + "/" + TrophyManager.TrophyType.values().length + ") ===");
        unlocked.stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage("[" + t.name().toLowerCase() + "] " + t.getDisplayName()));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trophy info <trophy>");
            return;
        }
        TrophyManager.TrophyType trophy = trophyManager.getTrophy(args[1]);
        if (trophy == null) {
            player.sendMessage("Unknown trophy: " + args[1] + ". Use /trophy list to see all trophies.");
            return;
        }
        boolean has = trophyManager.hasTrophy(player.getUniqueId(), trophy);
        player.sendMessage("=== " + trophy.getDisplayName() + " ===");
        player.sendMessage(trophy.getDescription());
        player.sendMessage("Status: " + (has ? "Unlocked" : "Locked"));
    }

    private void handleUnlock(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trophy unlock <trophy>");
            return;
        }
        TrophyManager.TrophyType trophy = trophyManager.getTrophy(args[1]);
        if (trophy == null) {
            player.sendMessage("Unknown trophy: " + args[1] + ". Use /trophy list to see all trophies.");
            return;
        }
        boolean isNew = trophyManager.unlockTrophy(player.getUniqueId(), trophy);
        if (isNew) {
            player.sendMessage("Trophy unlocked: " + trophy.getDisplayName() + "!");
        } else {
            player.sendMessage("You have already unlocked: " + trophy.getDisplayName() + ".");
        }
    }

    private void handleReset(Player player) {
        trophyManager.resetTrophies(player.getUniqueId());
        player.sendMessage("All your trophies have been reset.");
    }
}
