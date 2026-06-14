package com.skyblock.plugin.command.skills;

import com.skyblock.core.skills.SkillsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class SkillsCommand implements CommandExecutor {

    private final SkillsManager manager;

    public SkillsCommand(SkillsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Map<String, Integer> levels = manager.getSkillLevels(player.getUniqueId());
        player.sendMessage("=== Skills ===");
        for (String skill : SkillsManager.SKILLS) {
            int level = levels.getOrDefault(skill, 0);
            player.sendMessage("  " + skill + ": level " + level);
        }
        return true;
    }
}
