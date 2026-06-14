package com.skyblock.plugin.commands;

import com.skyblock.core.minion.MinionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class MinionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        MinionManager manager = MinionManager.getInstance();
        List<UUID> minionIds = manager.getMinions(id);
        player.sendMessage("=== Minions (" + minionIds.size() + "/" + MinionManager.MAX_SLOTS + ") ===");
        if (minionIds.isEmpty()) {
            player.sendMessage("You have no active minions.");
            return true;
        }
        for (UUID minionId : minionIds) {
            MinionManager.MinionData data = manager.getMinion(minionId);
            if (data == null) continue;
            player.sendMessage(data.type.getDisplayName() + " — Tier: " + (data.getTier().ordinal() + 1));
        }
        return true;
    }
}
