package com.skyblock.plugin.command.slayer;

import com.skyblock.core.slayer.SlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.commands.SlayerCommand} instead.
 */
@Deprecated
public final class SlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        SlayerManager manager = SlayerManager.getInstance();

        player.sendMessage("=== Your Slayers ===");
        for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
            int level = manager.getLevel(id, type);
            long xp = manager.getExperience(id, type);
            int kills = manager.getKillCount(id, type);
            player.sendMessage("  " + type.getDisplayName() + ": Level " + level + " (" + xp + " XP, " + kills + " kills)");
        }

        SlayerManager.SlayerQuest quest = manager.getActiveQuest(id);
        if (quest != null) {
            player.sendMessage("Active Quest: " + quest.type.getDisplayName() + " T" + (quest.tier.ordinal() + 1) + " — " + quest.getKills() + " kills");
        }
        return true;
    }
}
