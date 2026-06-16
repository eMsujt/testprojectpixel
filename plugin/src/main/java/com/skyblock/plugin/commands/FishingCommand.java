package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.FishingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Duplicate of {@link com.skyblock.core.fishing.FishingCommand}. Use that class instead.
 */
@Deprecated
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
            case "stats"   -> handleStats(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID uuid = player.getUniqueId();
        FishingManager mgr = FishingManager.getInstance();
        int xp = mgr.getFishingXp(uuid);
        Map<String, Long> counts = mgr.getFishCounts(uuid);
        player.sendMessage("=== Fishing Stats ===");
        player.sendMessage("XP: " + xp);
        if (counts.isEmpty()) {
            player.sendMessage("No fish caught yet.");
        } else {
            for (Map.Entry<String, Long> entry : counts.entrySet()) {
                player.sendMessage(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private void handleHistory(Player player) {
        List<String> history = FishingManager.getInstance().getCatchHistory(player.getUniqueId());
        player.sendMessage("=== Fishing History ===");
        if (history.isEmpty()) {
            player.sendMessage("You have no fishing history.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Fishing Commands ===");
        player.sendMessage("/skyblock fishing         — show your fishing stats");
        player.sendMessage("/skyblock fishing stats   — show your fishing stats");
        player.sendMessage("/skyblock fishing history — show your catch history");
    }
}
