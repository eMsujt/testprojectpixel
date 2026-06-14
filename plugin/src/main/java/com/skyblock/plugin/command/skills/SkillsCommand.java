package com.skyblock.plugin.command.skills;

import com.skyblock.core.skills.SkillsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SkillsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        SkillsManager manager = SkillsManager.getInstance();

        player.sendMessage("=== Your Skills ===");
        for (String skill : SkillsManager.SKILLS) {
            int level = manager.getLevel(id, skill);
            long xp = manager.getXp(id, skill);
            player.sendMessage("  " + capitalize(skill) + ": Level " + level + " (" + xp + " XP)");
        }
        return true;
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
