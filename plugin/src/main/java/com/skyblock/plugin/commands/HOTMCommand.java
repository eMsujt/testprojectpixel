package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.HOTMManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Duplicate of {@link com.skyblock.core.hotm.HOTMCommand}. Use that class instead.
 */
@Deprecated
public final class HOTMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleLevel(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "level"        -> handleLevel(player);
            case "set"          -> handleSet(player, args);
            case "stats"        -> handleStats(player);
            case "powder-stats" -> handlePowderStats(player);
            case "history"      -> handleHistory(player);
            default             -> sendHelp(player);
        }
        return true;
    }

    private void handleLevel(Player player) {
        UUID id = player.getUniqueId();
        int level = HOTMManager.getInstance().getHotmLevel(id);
        player.sendMessage("=== Heart of the Mountain ===");
        player.sendMessage("Your HOTM level: " + level);
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock hotm set <level>");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        HOTMManager.getInstance().setHotmLevel(player.getUniqueId(), level);
        player.sendMessage("HOTM level set to " + HOTMManager.getInstance().getHotmLevel(player.getUniqueId()) + ".");
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        HOTMManager mgr = HOTMManager.getInstance();
        player.sendMessage("=== HOTM Stats ===");
        player.sendMessage("Total Powder: " + mgr.getTotalPowder(id));
        player.sendMessage("Tokens Spent: " + mgr.getTokensSpent(id));
    }

    private void handlePowderStats(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Integer> collected = HOTMManager.getInstance().getPowderCollected(id);
        player.sendMessage("=== HOTM Powder Stats ===");
        if (collected.isEmpty()) {
            player.sendMessage("No powder collected yet.");
            return;
        }
        for (Map.Entry<String, Integer> entry : collected.entrySet()) {
            player.sendMessage(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = HOTMManager.getInstance().getUpgradeHistory(id);
        player.sendMessage("=== HOTM Upgrade History ===");
        if (history.isEmpty()) {
            player.sendMessage("No upgrades recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== HOTM Commands ===");
        player.sendMessage("/skyblock hotm level        — show your Heart of the Mountain level");
        player.sendMessage("/skyblock hotm set <level>  — set your HOTM level (1–7)");
        player.sendMessage("/skyblock hotm powder-stats — show powder collected by type");
        player.sendMessage("/skyblock hotm history      — show your HOTM upgrade history");
    }
}
