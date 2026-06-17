package com.skyblock.core.manager;

import com.skyblock.core.menu.HarpMenu;
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
 * Handles the {@code /harp} command (Melody's Harp).
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /harp}         — open the harp GUI menu (song list, unlock state, best completion)</li>
 *   <li>{@code /harp list}    — list every song with its unlock status and best completion in chat</li>
 *   <li>{@code /harp info}    — same as {@code list}</li>
 *   <li>{@code /harp stats}   — show completed song count and the earned Intelligence bonus</li>
 *   <li>{@code /harp reset}   — reset all harp progress</li>
 * </ul>
 * </p>
 */
public final class HarpCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "info", "stats", "reset");

    private final HarpManager harpManager;

    public HarpCommand(HarpManager harpManager) {
        this.harpManager = harpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new HarpMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"  -> handleInfo(player);
            case "info"  -> handleInfo(player);
            case "stats" -> handleStats(player);
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
        player.sendMessage("=== Melody's Harp Songs ===");
        for (HarpManager.Song song : HarpManager.Song.values()) {
            if (!harpManager.isUnlocked(id, song)) {
                player.sendMessage("  " + song.getDisplayName() + ": Locked");
                continue;
            }
            int best = harpManager.getBestCompletion(id, song);
            String status = harpManager.isCompleted(id, song) ? "Completed" : best + "%";
            player.sendMessage("  " + song.getDisplayName() + ": " + status);
        }
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Harp Progress ===");
        player.sendMessage("Songs completed: " + harpManager.getCompletedCount(id)
                + " / " + HarpManager.Song.values().length);
        player.sendMessage("Intelligence bonus: +" + harpManager.getIntelligenceBonus(id));
    }

    private void handleReset(Player player) {
        harpManager.reset(player.getUniqueId());
        player.sendMessage("Your harp progress has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Harp Commands ===");
        player.sendMessage("/harp        — open the harp menu");
        player.sendMessage("/harp list   — list songs and best completions");
        player.sendMessage("/harp info   — list songs and best completions");
        player.sendMessage("/harp stats  — show completed count and Intelligence bonus");
        player.sendMessage("/harp reset  — reset all harp progress");
    }
}
