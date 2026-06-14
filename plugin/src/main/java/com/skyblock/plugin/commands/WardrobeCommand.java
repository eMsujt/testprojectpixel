package com.skyblock.plugin.commands;

import com.skyblock.core.wardrobe.WardrobeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public final class WardrobeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        WardrobeManager manager = WardrobeManager.getInstance();
        Set<String> outfitNames = manager.getOutfitNames(id);

        player.sendMessage("=== Wardrobe (" + outfitNames.size() + "/" + WardrobeManager.MAX_OUTFITS + ") ===");
        if (outfitNames.isEmpty()) {
            player.sendMessage("You have no saved outfits.");
            return true;
        }
        for (String name : outfitNames) {
            player.sendMessage("- " + name);
        }
        return true;
    }
}
