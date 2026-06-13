package com.skyblock.core.command;

import com.skyblock.core.quest.QuestManager;
import com.skyblock.core.quest.QuestManager.QuestData;
import com.skyblock.core.quest.QuestManager.QuestStatus;
import com.skyblock.core.quest.QuestManager.QuestType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /quest} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /quest list}                   — list all available quest types</li>
 *   <li>{@code /quest start <type> <goal>}    — start a quest with a target goal</li>
 *   <li>{@code /quest status [type]}          — show status/progress for one or all quests</li>
 *   <li>{@code /quest reset}                  — reset all quest progress</li>
 * </ul>
 * </p>
 */
public final class QuestCommand implements TabExecutor {

    private final QuestManager questManager;

    public QuestCommand(QuestManager questManager) {
        this.questManager = questManager;
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
            case "list"   -> handleList(player);
            case "start"  -> handleStart(player, args);
            case "status" -> handleStatus(player, args);
            case "reset"  -> handleReset(player, args);
            default       -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "start", "status", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("start") || sub.equals("status")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(QuestType.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Quest Types ===");
        for (QuestType type : QuestType.values()) {
            player.sendMessage("- " + type.name().toLowerCase());
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /quest start <type> <goal>");
            return;
        }
        QuestType type;
        try {
            type = QuestType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown quest type: " + args[1]);
            return;
        }
        long goal;
        try {
            goal = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Goal must be a number, got: " + args[2]);
            return;
        }
        if (goal <= 0) {
            player.sendMessage("Goal must be a positive number.");
            return;
        }
        QuestData existing = questManager.getQuestData(player.getUniqueId(), type);
        if (existing != null && existing.status == QuestStatus.IN_PROGRESS) {
            player.sendMessage("Quest " + type.name() + " is already in progress.");
            return;
        }
        questManager.startQuest(player.getUniqueId(), type, goal);
        player.sendMessage("Quest started: " + type.name() + " (goal: " + goal + ")");
    }

    private void handleStatus(Player player, String[] args) {
        if (args.length >= 2) {
            QuestType type;
            try {
                type = QuestType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown quest type: " + args[1]);
                return;
            }
            sendQuestStatus(player, type);
            return;
        }
        player.sendMessage("=== Quest Status ===");
        boolean any = false;
        for (QuestType type : QuestType.values()) {
            QuestData data = questManager.getQuestData(player.getUniqueId(), type);
            if (data != null) {
                player.sendMessage(type.name() + ": " + data.progress + "/" + data.goal
                        + " [" + data.status.name() + "]");
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have no active quests.");
        }
    }

    private void sendQuestStatus(Player player, QuestType type) {
        QuestData data = questManager.getQuestData(player.getUniqueId(), type);
        if (data == null) {
            player.sendMessage("Quest " + type.name() + " has not been started.");
            return;
        }
        player.sendMessage("=== " + type.name() + " ===");
        player.sendMessage("Progress: " + data.progress + "/" + data.goal);
        player.sendMessage("Status: " + data.status.name());
    }

    private void handleReset(Player player, String[] args) {
        questManager.reset(player.getUniqueId());
        player.sendMessage("All quest progress has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Quest Commands ===");
        player.sendMessage("/quest list — list all quest types");
        player.sendMessage("/quest start <type> <goal> — start a quest");
        player.sendMessage("/quest status [type] — show quest progress");
        player.sendMessage("/quest reset — reset all quest progress");
    }
}
