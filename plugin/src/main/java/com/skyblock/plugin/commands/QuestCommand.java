package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.QuestManager;
import com.skyblock.plugin.managers.QuestManager.Quest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class QuestCommand implements CommandExecutor {

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
            case "list"    -> handleList(player);
            case "start"   -> handleStart(player, args);
            case "status"  -> handleStatus(player);
            case "abandon" -> handleAbandon(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        player.sendMessage("=== Available Quests ===");
        for (Quest quest : Quest.values()) {
            player.sendMessage("  " + quest.name());
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock quest start <quest>");
            return;
        }
        Quest quest;
        try {
            quest = Quest.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown quest: " + args[1] + ". Use /skyblock quest list to see available quests.");
            return;
        }
        UUID id = player.getUniqueId();
        if (QuestManager.getInstance().hasActiveQuest(id)) {
            player.sendMessage("You already have an active quest. Abandon it first with /skyblock quest abandon.");
            return;
        }
        QuestManager.getInstance().startQuest(id, quest);
        player.sendMessage("Quest started: " + quest.name());
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        Quest active = QuestManager.getInstance().getActiveQuest(id);
        if (active != null) {
            player.sendMessage("Active quest: " + active.name());
        } else {
            Quest last = QuestManager.getInstance().getLastCompletedQuest(id);
            if (last != null) {
                player.sendMessage("No active quest. Last completed: " + last.name());
            } else {
                player.sendMessage("You have no active quests. Use /skyblock quest start <quest> to begin one.");
            }
        }
    }

    private void handleAbandon(Player player) {
        UUID id = player.getUniqueId();
        if (QuestManager.getInstance().hasActiveQuest(id)) {
            QuestManager.getInstance().removePlayer(id);
            player.sendMessage("Your active quest has been abandoned.");
        } else {
            player.sendMessage("You have no active quest to abandon.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Quest Commands ===");
        player.sendMessage("/skyblock quest list           — list all available quests");
        player.sendMessage("/skyblock quest start <quest>  — start a quest");
        player.sendMessage("/skyblock quest status         — show your active quest");
        player.sendMessage("/skyblock quest abandon        — abandon your active quest");
    }
}
