package com.skyblock.plugin.command.mayor;

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

        MayorManager.MayorCandidate current = manager.getCurrentMayor();
        player.sendMessage("=== Mayor ===");
        if (current != null) {
            player.sendMessage("Current Mayor: " + current.getDisplayName());
            player.sendMessage("Perks: " + String.join(", ", current.getPerks()));
        } else {
            player.sendMessage("Current Mayor: None");
        }

        MayorManager.MayorCandidate vote = manager.getVote(id);
        player.sendMessage("Your Vote: " + (vote != null ? vote.getDisplayName() : "None"));
        return true;
    }
}
