package com.skyblock.plugin.commands;

import com.skyblock.core.quest.QuestManager;
import com.skyblock.core.quest.QuestManager.QuestData;
import com.skyblock.core.quest.QuestManager.QuestStatus;
import com.skyblock.core.quest.QuestManager.QuestType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SkyblockQuestCommand implements CommandExecutor {

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
            case "abandon" -> handleAbandon(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        player.sendMessage("=== Available Quests ===");
        for (QuestType type : QuestType.values()) {
            player.sendMessage("  " + type.name() + " — " + type.getDisplayName() + " (goal: " + type.getGoal() + ")");
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock quest start <quest>");
            return;
        }
        QuestType type;
        try {
            type = QuestType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown quest: " + args[1] + ". Use /skyblock quest list to see available quests.");
            return;
        }
        UUID id = player.getUniqueId();
        QuestStatus current = QuestManager.getInstance().getStatus(id, type);
        if (current == QuestStatus.IN_PROGRESS) {
            player.sendMessage("You already have that quest in progress.");
            return;
        }
        QuestManager.getInstance().startQuest(id, type);
        player.sendMessage("Quest started: " + type.getDisplayName() + " (goal: " + type.getGoal() + ")");
    }

    private void handleStatus(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2) {
            QuestType type;
            try {
                type = QuestType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown quest: " + args[1] + ". Use /skyblock quest list to see available quests.");
                return;
            }
            QuestData data = QuestManager.getInstance().getQuestData(id, type);
            if (data == null) {
                player.sendMessage("You have not started that quest.");
                return;
            }
            player.sendMessage("=== " + type.getDisplayName() + " ===");
            player.sendMessage("Status: " + data.status.name());
            player.sendMessage("Progress: " + data.progress + " / " + data.goal);
            return;
        }

        player.sendMessage("=== Your Quests ===");
        boolean any = false;
        for (QuestType type : QuestType.values()) {
            QuestData data = QuestManager.getInstance().getQuestData(id, type);
            if (data != null) {
                player.sendMessage("  " + type.getDisplayName() + " — " + data.status.name()
                        + " (" + data.progress + "/" + data.goal + ")");
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have no active quests. Use /skyblock quest start <quest> to begin one.");
        }
    }

    private void handleAbandon(Player player) {
        boolean had = QuestManager.getInstance().reset(player.getUniqueId());
        if (had) {
            player.sendMessage("All your quests have been abandoned.");
        } else {
            player.sendMessage("You have no active quests to abandon.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Quest Commands ===");
        player.sendMessage("/skyblock quest list              — list all available quests");
        player.sendMessage("/skyblock quest start <quest>     — start a quest");
        player.sendMessage("/skyblock quest status [quest]    — show your quest progress");
        player.sendMessage("/skyblock quest abandon           — abandon all active quests");
    }
}
