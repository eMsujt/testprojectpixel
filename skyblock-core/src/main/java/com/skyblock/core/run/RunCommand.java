package com.skyblock.core.run;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the {@code /run} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /run info [floor]}  — show run counts for all floors or a specific floor</li>
 *   <li>{@code /run add <floor>}   — record one completed run on the given floor</li>
 *   <li>{@code /run reset}         — clear all run counts</li>
 * </ul>
 * </p>
 */
public final class RunCommand implements TabExecutor {

    private final RunManager runManager;

    public RunCommand(RunManager runManager) {
        this.runManager = runManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player, args);
            case "add"   -> handleAdd(player, args);
            case "reset" -> handleReset(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("info", "add", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length >= 2) {
            String floor = args[1].toUpperCase();
            int count = runManager.getRuns(player.getUniqueId(), floor);
            player.sendMessage("Runs on " + floor + ": " + count);
            return;
        }
        Map<String, Integer> all = runManager.getAllRuns(player.getUniqueId());
        if (all.isEmpty()) {
            player.sendMessage("No dungeon runs recorded.");
            return;
        }
        player.sendMessage("=== Dungeon Runs ===");
        all.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(e.getKey() + ": " + e.getValue()));
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /run add <floor>");
            return;
        }
        String floor = args[1].toUpperCase();
        runManager.addRun(player.getUniqueId(), floor);
        player.sendMessage("Recorded run on " + floor + ". Total: " + runManager.getRuns(player.getUniqueId(), floor));
    }

    private void handleReset(Player player) {
        runManager.resetRuns(player.getUniqueId());
        player.sendMessage("All dungeon run counts have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Run Commands ===");
        player.sendMessage("/run info [floor] — view run counts");
        player.sendMessage("/run add <floor> — record a completed run");
        player.sendMessage("/run reset — clear all run counts");
    }
}
