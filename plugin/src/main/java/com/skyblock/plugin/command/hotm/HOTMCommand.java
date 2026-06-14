package com.skyblock.plugin.command.hotm;

import com.skyblock.core.hotm.HOTMManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class HOTMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
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
}
