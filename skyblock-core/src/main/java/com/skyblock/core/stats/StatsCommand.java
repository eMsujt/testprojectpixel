package com.skyblock.core.stats;

import com.skyblock.core.skills.SkillsManager;
import com.skyblock.core.slayer.SlayerManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class StatsCommand implements TabExecutor {

    private final SkillsManager skillsManager;
    private final SlayerManager slayerManager;

    public StatsCommand(SkillsManager skillsManager, SlayerManager slayerManager) {
        this.skillsManager = skillsManager;
        this.slayerManager = slayerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        showStats(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    private void showStats(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("§6§l--- SkyBlock Stats ---");

        player.sendMessage("§e§lSkills:");
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            int level = skillsManager.getLevel(id, skill);
            double xp = skillsManager.getXp(id, skill);
            player.sendMessage("  §7" + skill.getDisplayName() + ": §fLevel " + level + " §8(§7" + (long) xp + " XP§8)");
        }

        player.sendMessage("§e§lSlayer:");
        for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
            int level = slayerManager.getLevel(id, type);
            long xp = slayerManager.getExperience(id, type);
            player.sendMessage("  §7" + type.getDisplayName() + ": §fLevel " + level + " §8(§7" + xp + " XP§8)");
        }
    }
}
