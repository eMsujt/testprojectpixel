package com.skyblock.core.run;

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
 * Handles the {@code /run} command for dungeon run statistics.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /run info [floor]} — show run counts, optionally for one floor</li>
 *   <li>{@code /run reset}        — reset all run counts</li>
 * </ul>
 * </p>
 */
public final class RunCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "reset");

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

        if (args.length == 0 || args[0].equalsIgnoreCase("info")) {
            handleInfo(player, args);
            return true;
        }

        switch (args[0].toLowerCase()) {
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

    private void handleInfo(Player player, String[] args) {
        if (args.length >= 2) {
            String floor = args[1];
            int count = runManager.getRunCount(player.getUniqueId(), floor);
            player.sendMessage("Runs on " + floor + ": " + count);
        } else {
            Map<String, Integer> runs = runManager.getRuns(player.getUniqueId());
            if (runs.isEmpty()) {
                player.sendMessage("You have no recorded dungeon runs.");
                return;
            }
            player.sendMessage("=== Your Dungeon Runs ===");
            runs.forEach((floor, count) ->
                    player.sendMessage(floor + ": " + count));
        }
    }

    private void handleReset(Player player) {
        runManager.resetRuns(player.getUniqueId());
        player.sendMessage("Your dungeon run statistics have been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Run Commands ===");
        player.sendMessage("/run info [floor]  — view run statistics");
        player.sendMessage("/run reset         — reset all run counts");
    }
}
