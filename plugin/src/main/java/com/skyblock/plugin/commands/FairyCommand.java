package com.skyblock.plugin.commands;

import com.skyblock.core.fairysoul.FairySoulManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.command.fairy.FairyCommand} instead.
 */
@Deprecated
public final class FairyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        FairySoulManager manager = FairySoulManager.getInstance();
        player.sendMessage("=== Fairy Souls ===");
        player.sendMessage("Collected: " + manager.getCount(id) + " / " + FairySoulManager.MAX_SOULS);
        for (FairySoulManager.FairySoulArea area : FairySoulManager.FairySoulArea.values()) {
            player.sendMessage("  " + area.getDisplayName() + ": " + area.soulCount + " souls");
        }
        return true;
    }
}
