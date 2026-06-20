package com.skyblock.core.command;

import com.skyblock.core.manager.RunecraftingManager;
import com.skyblock.core.manager.RunecraftingManager.RuneType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class RunecraftingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        RunecraftingManager manager = RunecraftingManager.getInstance();
        UUID id = player.getUniqueId();
        player.sendMessage("§5§lRunecrafting");
        player.sendMessage("§7Level: §d" + manager.getSkillLevel(id)
                + "§7/§d" + RunecraftingManager.MAX_SKILL_LEVEL
                + " §8(§d" + manager.getSkillXp(id) + " XP§8)");
        for (RuneType type : RuneType.values()) {
            int count = manager.getRuneCount(id, type);
            if (count > 0) {
                player.sendMessage("§7" + type.name() + ": §dx" + count
                        + " §8(lvl " + manager.getRuneLevel(id, type) + ")");
            }
        }
        return true;
    }
}
