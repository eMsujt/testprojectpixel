package com.skyblock.plugin.command.hotm;

import com.skyblock.core.hotm.HOTMManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class HOTMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("history")) {
            handleHistory(player);
            return true;
        }

        UUID id = player.getUniqueId();
        HOTMManager manager = HOTMManager.getInstance();

        player.sendMessage("=== Heart of the Mountain ===");
        player.sendMessage("Mithril Powder: " + manager.getMithrilPowder(id));
        player.sendMessage("Gemstone Powder: " + manager.getGemstonePowder(id));
        player.sendMessage("Perks:");
        for (HOTMManager.HOTMPerk perk : HOTMManager.HOTMPerk.values()) {
            int level = manager.getLevel(id, perk);
            player.sendMessage("  " + perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
        }
        return true;
    }

    private void handleHistory(Player player) {
        List<String> history = HOTMManager.getInstance().getHotmHistory(player.getUniqueId());
        player.sendMessage("=== HOTM History ===");
        if (history.isEmpty()) {
            player.sendMessage("No HOTM events recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }
}
