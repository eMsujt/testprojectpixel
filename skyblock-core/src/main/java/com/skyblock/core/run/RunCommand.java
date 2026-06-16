package com.skyblock.core.run;

import com.skyblock.core.dungeon.manager.RunManager;
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
 * Handles the {@code /runs} command for dungeon run statistics.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /runs stats [floor]} — show run counts for all floors or a specific floor</li>
 *   <li>{@code /runs add <floor>}   — increment run count for a floor</li>
 *   <li>{@code /runs reset}         — reset all run counts</li>
 * </ul>
 * </p>
 */
public final class RunCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("stats", "add", "reset");

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
            case "stats" -> handleStats(player, args);
            case "add"   -> handleAdd(player, args);
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

    private void handleStats(Player player, String[] args) {
        if (args.length >= 2) {
            String floor = args[1];
            int count = runManager.getRunCount(player.getUniqueId(), floor);
            player.sendMessage("Runs on " + floor + ": " + count);
        } else {
            Map<String, Integer> runs = runManager.getRunCounts(player.getUniqueId());
            if (runs.isEmpty()) {
                player.sendMessage("You have no recorded dungeon runs.");
                return;
            }
            player.sendMessage("=== Your Dungeon Runs ===");
            runs.forEach((floor, count) ->
                    player.sendMessage(floor + ": " + count));
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /runs add <floor>");
            return;
        }
        String floor = args[1];
        int total = runManager.incrementRunCount(player.getUniqueId(), floor);
        player.sendMessage("Run added for floor: " + floor + " (total: " + total + ")");
    }

    private void handleReset(Player player) {
        runManager.reset(player.getUniqueId());
        player.sendMessage("Your dungeon run statistics have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Runs Commands ===");
        player.sendMessage("/runs stats [floor] — view run statistics");
        player.sendMessage("/runs add <floor>   — add a run for a floor");
        player.sendMessage("/runs reset         — reset all run counts");
    }
}
