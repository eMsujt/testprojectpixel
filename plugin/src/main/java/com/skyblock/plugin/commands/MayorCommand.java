package com.skyblock.plugin.commands;

import com.skyblock.core.mayor.MayorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class MayorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        MayorManager manager = MayorManager.getInstance();
        MayorManager.MayorCandidate currentMayor = manager.getCurrentMayor();
        MayorManager.MayorCandidate vote = manager.getVote(id);
        player.sendMessage("=== Mayor ===");
        player.sendMessage("Current Mayor: " + (currentMayor != null ? currentMayor.getDisplayName() : "None"));
        if (currentMayor != null) {
            player.sendMessage("Perks: " + String.join(", ", currentMayor.getPerks()));
        }
        player.sendMessage("Your Vote: " + (vote != null ? vote.getDisplayName() : "None"));
        return true;
    }
}
