package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.FishingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class FishingCommand implements CommandExecutor {

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
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        FishingManager manager = FishingManager.getInstance();
        Map<String, Long> counts = manager.getFishCounts(id);

        player.sendMessage("=== Fishing Stats ===");
        if (counts.isEmpty()) {
            player.sendMessage("You have not caught any fish yet.");
        } else {
            for (Map.Entry<String, Long> entry : counts.entrySet()) {
                player.sendMessage(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Fishing Commands ===");
        player.sendMessage("/fishing stats — list all fish you have caught and their counts");
    }
}
