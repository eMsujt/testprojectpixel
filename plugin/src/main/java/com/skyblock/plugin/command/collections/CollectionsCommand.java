package com.skyblock.plugin.command.collections;

import com.skyblock.core.collections.CollectionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class CollectionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        CollectionsManager manager = new CollectionsManager();

        Map<String, Long> amounts = manager.getCollectionAmounts(id);
        Map<String, Integer> tiers = manager.getCollectionTiers(id);

        player.sendMessage("=== Your Collections ===");
        for (String collection : CollectionsManager.COLLECTIONS) {
            long amount = amounts.getOrDefault(collection, 0L);
            int tier = tiers.getOrDefault(collection, 0);
            player.sendMessage("  " + collection + ": " + amount + " (Tier " + tier + ")");
        }
        return true;
    }
}
