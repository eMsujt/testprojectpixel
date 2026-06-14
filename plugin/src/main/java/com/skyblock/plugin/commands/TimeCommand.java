package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.TimeManager;
import com.skyblock.plugin.managers.TimeManager.SkyblockTime;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TimeCommand implements CommandExecutor {

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
            case "set"    -> handleSet(player, args);
            case "get"    -> handleGet(player);
            case "list"   -> handleList(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock time set <phase>");
            return;
        }
        SkyblockTime time;
        try {
            time = SkyblockTime.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown time phase: " + args[1] + ". Use /skyblock time list to see valid phases.");
            return;
        }
        TimeManager.getInstance().setCurrentTime(time);
        player.sendMessage("SkyBlock time set to: " + time.name());
    }

    private void handleGet(Player player) {
        SkyblockTime current = TimeManager.getInstance().getCurrentTime();
        player.sendMessage("Current SkyBlock time: " + current.name());
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Time Phases ===");
        for (SkyblockTime time : SkyblockTime.values()) {
            player.sendMessage("  " + time.name());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Time Commands ===");
        player.sendMessage("/skyblock time set <phase>   — set the current SkyBlock time phase");
        player.sendMessage("/skyblock time get           — show the current SkyBlock time phase");
        player.sendMessage("/skyblock time list          — list all valid time phases");
    }
}
