package com.skyblock.core.command;

import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.FloorRecord;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;

public final class DungeonCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("floor", "boss", "stats", "history");

    private final DungeonManager manager;

    public DungeonCommand(DungeonManager manager) {
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
            case "floor"   -> handleFloor(player, args);
            case "boss"    -> handleBoss(player, args);
            case "stats"   -> handleStats(player);
            case "history" -> handleHistory(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("floor") || args[0].equalsIgnoreCase("boss"))) {
            String prefix = args[1];
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7").stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleFloor(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon floor <1-7>");
            return;
        }
        int floor;
        try {
            floor = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid floor number.");
            return;
        }
        if (floor < 1 || floor > 7) {
            player.sendMessage("Floor must be between 1 and 7.");
            return;
        }
        int completions = manager.getCompletions(player.getUniqueId(), floor);
        OptionalInt bestScore = manager.getBestScore(player.getUniqueId(), floor);
        player.sendMessage("=== Floor " + floor + " ===");
        player.sendMessage("Completions: " + completions);
        player.sendMessage("Best Score: " + (bestScore.isPresent() ? bestScore.getAsInt() : "none"));
    }

    private void handleBoss(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon boss <1-7>");
            return;
        }
        int floor;
        try {
            floor = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid floor number.");
            return;
        }
        String bossName = switch (floor) {
            case 1 -> "Bonzo";
            case 2 -> "Scarf";
            case 3 -> "The Professor";
            case 4 -> "Thorn";
            case 5 -> "Livid";
            case 6 -> "Sadan";
            case 7 -> "Necron";
            default -> null;
        };
        if (bossName == null) {
            player.sendMessage("Floor must be between 1 and 7.");
            return;
        }
        player.sendMessage("Floor " + floor + " Boss: " + bossName);
    }

    private void handleStats(Player player) {
        Map<Integer, FloorRecord> records = manager.getRecords(player.getUniqueId());
        if (records.isEmpty()) {
            player.sendMessage("You have no dungeon completions yet.");
            return;
        }
        OptionalInt highest = manager.getHighestCompletedFloor(player.getUniqueId());
        player.sendMessage("=== Dungeon Stats ===");
        if (highest.isPresent()) {
            player.sendMessage("Highest Floor: " + highest.getAsInt());
        }
        for (Map.Entry<Integer, FloorRecord> entry : records.entrySet()) {
            FloorRecord rec = entry.getValue();
            player.sendMessage("Floor " + entry.getKey() + ": "
                    + rec.getCompletions() + " run(s), best score: " + rec.getBestScore());
        }
    }

    private void handleHistory(Player player) {
        UUID uuid = player.getUniqueId();
        List<String> history = manager.getDungeonHistory(uuid);
        if (history.isEmpty()) {
            player.sendMessage("You have no dungeon history yet.");
            return;
        }
        player.sendMessage("=== Dungeon History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon floor <1-7> — view your stats for a floor");
        player.sendMessage("/dungeon boss <1-7> — show the boss for a floor");
        player.sendMessage("/dungeon stats — view all your dungeon completions");
        player.sendMessage("/dungeon history — view your dungeon run history");
    }
}
