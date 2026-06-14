package com.skyblock.core.command;

import com.skyblock.core.dungeon.DungeonManager;
import com.skyblock.core.dungeon.DungeonManager.DungeonClass;
import com.skyblock.core.dungeon.DungeonManager.DungeonRun;
import com.skyblock.core.dungeon.DungeonManager.DungeonType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /dungeon} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /dungeon join <type>}   — join a dungeon run of the given type</li>
 *   <li>{@code /dungeon leave}         — abandon the current dungeon run</li>
 *   <li>{@code /dungeon status}        — show the current active run info</li>
 *   <li>{@code /dungeon stats [type]}  — show best score and completion count</li>
 *   <li>{@code /dungeon class <class>} — select a dungeon class</li>
 * </ul>
 * </p>
 */
public final class DungeonCommand implements TabExecutor {

    private final DungeonManager dungeonManager;

    public DungeonCommand(DungeonManager dungeonManager) {
        this.dungeonManager = dungeonManager;
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
            case "join"    -> handleJoin(player, args);
            case "leave"   -> handleLeave(player);
            case "status"  -> handleStatus(player);
            case "stats"   -> handleStats(player, args);
            case "class"   -> handleClass(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("join", "leave", "status", "stats", "class", "history").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("class")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(DungeonClass.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("stats"))) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(DungeonType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon join <type>");
            return;
        }
        DungeonType type;
        try {
            type = DungeonType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown dungeon type: " + args[1]);
            return;
        }
        DungeonRun existing = dungeonManager.getActiveRun(player.getUniqueId());
        if (existing != null) {
            player.sendMessage("You are already in a dungeon run. Use /dungeon leave first.");
            return;
        }
        dungeonManager.startRun(type, Collections.singletonList(player.getUniqueId()), System.currentTimeMillis());
        player.sendMessage("You have joined dungeon: " + type.name());
    }

    private void handleLeave(Player player) {
        DungeonRun run = dungeonManager.getActiveRun(player.getUniqueId());
        if (run == null) {
            player.sendMessage("You are not in a dungeon run.");
            return;
        }
        dungeonManager.abandonRun(player.getUniqueId());
        player.sendMessage("You have left the dungeon run.");
    }

    private void handleStatus(Player player) {
        DungeonRun run = dungeonManager.getActiveRun(player.getUniqueId());
        if (run == null) {
            player.sendMessage("You are not in a dungeon run.");
            return;
        }
        player.sendMessage("=== Dungeon Status ===");
        player.sendMessage("Type: " + run.getType().name());
        player.sendMessage("Participants: " + run.getParticipants().size());
        player.sendMessage("Completed: " + run.isCompleted());
        if (run.isCompleted()) {
            player.sendMessage("Score: " + run.getScore());
        }
    }

    private void handleStats(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("=== Dungeon Stats ===");
            for (DungeonType type : DungeonType.values()) {
                int best = dungeonManager.getBestScore(player.getUniqueId(), type);
                int count = dungeonManager.getCompletionCount(player.getUniqueId(), type);
                if (count > 0) {
                    player.sendMessage(type.name() + ": " + count + " completions, best score " + best);
                }
            }
            return;
        }
        DungeonType type;
        try {
            type = DungeonType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown dungeon type: " + args[1]);
            return;
        }
        int best = dungeonManager.getBestScore(player.getUniqueId(), type);
        int count = dungeonManager.getCompletionCount(player.getUniqueId(), type);
        player.sendMessage("=== " + type.name() + " Stats ===");
        player.sendMessage("Completions: " + count);
        player.sendMessage("Best score: " + best);
    }

    private void handleClass(Player player, String[] args) {
        if (args.length < 2) {
            DungeonClass current = dungeonManager.getClass(player.getUniqueId());
            player.sendMessage("Current class: " + (current != null ? current.name() : "none"));
            player.sendMessage("Usage: /dungeon class <HEALER|MAGE|BERSERK|ARCHER|TANK>");
            return;
        }
        DungeonClass dungeonClass;
        try {
            dungeonClass = DungeonClass.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown class: " + args[1]);
            return;
        }
        dungeonManager.setClass(player.getUniqueId(), dungeonClass);
        player.sendMessage("Dungeon class set to: " + dungeonClass.name());
    }

    private void handleHistory(Player player) {
        List<String> history = dungeonManager.getDungeonHistory(player.getUniqueId());
        player.sendMessage("=== Dungeon History ===");
        if (history.isEmpty()) {
            player.sendMessage("No dungeon history found.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon join <type> — join a dungeon run");
        player.sendMessage("/dungeon leave — abandon the current run");
        player.sendMessage("/dungeon status — show current run info");
        player.sendMessage("/dungeon stats [type] — show completion stats");
        player.sendMessage("/dungeon class <class> — select your dungeon class");
        player.sendMessage("/dungeon history — view your dungeon run history");
    }
}
