package com.skyblock.plugin.command.fairy;

import com.skyblock.core.manager.FairySoulManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class FairyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        FairySoulManager manager = FairySoulManager.getInstance();

        int count = manager.getFoundCount(id);
        player.sendMessage("=== Fairy Souls ===");
        player.sendMessage("Collected: " + count + " / " + manager.getTotalSouls());
        return true;
    }
}
