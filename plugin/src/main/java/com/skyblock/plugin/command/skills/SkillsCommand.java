package com.skyblock.plugin.command.skills;

import com.skyblock.core.skills.SkillsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SkillsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        SkillsManager manager = SkillsManager.getInstance();
        player.sendMessage("=== Skills ===");
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            int level = manager.getLevel(player.getUniqueId(), skill);
            player.sendMessage(skill.getDisplayName() + ": " + level);
        }
        return true;
    }
}
