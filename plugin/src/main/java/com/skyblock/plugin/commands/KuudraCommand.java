package com.skyblock.plugin.commands;

import com.skyblock.core.kuudra.KuudraManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KuudraCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        player.sendMessage("=== Kuudra ===");
        for (KuudraManager.KuudraTier tier : KuudraManager.KuudraTier.values()) {
            int completions = manager.getCompletionCount(id, tier);
            player.sendMessage(tier.getDisplayName() + " — Completions: " + completions);
        }
        return true;
    }
}
