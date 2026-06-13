package com.skyblock.core.dungeon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DungeonCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "start", "leave", "complete", "class", "scores");

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
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"     -> handleInfo(player);
            case "start"    -> handleStart(player, args);
            case "leave"    -> handleLeave(player);
            case "complete" -> handleComplete(player, args);
            case "class"    -> handleClass(player, args);
            case "scores"   -> handleScores(player, args);
            default         -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(DungeonManager.DungeonType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("class")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(DungeonManager.DungeonClass.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("scores")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(DungeonManager.DungeonType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        DungeonManager.DungeonRun run = dungeonManager.getActiveRun(player.getUniqueId());
        DungeonManager.DungeonClass cls = dungeonManager.getClass(player.getUniqueId());
        player.sendMessage("=== Dungeon Info ===");
        player.sendMessage("  Class      : " + (cls != null ? cls.name() : "none"));
        if (run != null) {
            player.sendMessage("  Active run : " + run.getType().name());
            player.sendMessage("  Participants: " + run.getParticipants().size());
        } else {
            player.sendMessage("  Active run : none");
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon start <type>");
            return;
        }
        DungeonManager.DungeonType type;
        try {
            type = DungeonManager.DungeonType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown dungeon type: " + args[1]);
            return;
        }
        dungeonManager.startRun(type, Collections.singletonList(player.getUniqueId()), System.currentTimeMillis());
        player.sendMessage("Started dungeon run: " + type.name());
    }

    private void handleLeave(Player player) {
        dungeonManager.abandonRun(player.getUniqueId());
        player.sendMessage("You have left the dungeon run.");
    }

    private void handleComplete(Player player, String[] args) {
        int score = 0;
        if (args.length >= 2) {
            try {
                score = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid score: " + args[1]);
                return;
            }
        }
        try {
            dungeonManager.completeRun(player.getUniqueId(), score);
            player.sendMessage("Dungeon run completed with score: " + score);
        } catch (IllegalStateException e) {
            player.sendMessage("You are not in a dungeon run.");
        }
    }

    private void handleClass(Player player, String[] args) {
        if (args.length < 2) {
            DungeonManager.DungeonClass cls = dungeonManager.getClass(player.getUniqueId());
            player.sendMessage("Your dungeon class: " + (cls != null ? cls.name() : "none"));
            return;
        }
        DungeonManager.DungeonClass cls;
        try {
            cls = DungeonManager.DungeonClass.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown class: " + args[1]);
            return;
        }
        dungeonManager.setClass(player.getUniqueId(), cls);
        player.sendMessage("Dungeon class set to: " + cls.name());
    }

    private void handleScores(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon scores <type>");
            return;
        }
        DungeonManager.DungeonType type;
        try {
            type = DungeonManager.DungeonType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown dungeon type: " + args[1]);
            return;
        }
        int best = dungeonManager.getBestScore(player.getUniqueId(), type);
        int count = dungeonManager.getCompletionCount(player.getUniqueId(), type);
        player.sendMessage("=== " + type.name() + " ===");
        player.sendMessage("  Best score  : " + best);
        player.sendMessage("  Completions : " + count);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon info              — show active run and class");
        player.sendMessage("/dungeon start <type>      — start a dungeon run");
        player.sendMessage("/dungeon leave             — abandon your current run");
        player.sendMessage("/dungeon complete [score]  — complete your current run");
        player.sendMessage("/dungeon class [class]     — view or set your dungeon class");
        player.sendMessage("/dungeon scores <type>     — view your scores for a dungeon");
    }
}
