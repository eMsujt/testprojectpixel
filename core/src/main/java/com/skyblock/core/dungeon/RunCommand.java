package com.skyblock.core.dungeon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RunCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("stats", "start", "stop");

    private final RunManager manager;

    public RunCommand(RunManager manager) {
        this.manager = manager;
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
            case "stats" -> handleStats(player);
            case "start" -> handleStart(player);
            case "stop"  -> handleStop(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStats(Player player) {
        Map<String, Integer> stats = manager.getStats(player.getUniqueId());
        if (stats.isEmpty()) {
            player.sendMessage("You have no dungeon run stats yet.");
            return;
        }
        player.sendMessage("=== Run Stats ===");
        stats.forEach((stat, value) -> player.sendMessage(stat + ": " + value));
    }

    private void handleStart(Player player) {
        manager.incrementStat(player.getUniqueId(), "runs_started");
        player.sendMessage("Dungeon run started! Good luck.");
    }

    private void handleStop(Player player) {
        manager.incrementStat(player.getUniqueId(), "runs_stopped");
        player.sendMessage("Dungeon run stopped.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Run Commands ===");
        player.sendMessage("/run start — start a dungeon run");
        player.sendMessage("/run stop — stop your current run");
        player.sendMessage("/run stats — view your run statistics");
    }
}
