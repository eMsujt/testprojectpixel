package com.skyblock.core.skill;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /skill} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /skill}          — lists all skill levels for the sender</li>
 *   <li>{@code /skill <skill>}  — shows XP details for a specific skill</li>
 * </ul>
 * </p>
 */
public final class SkillCommand implements TabExecutor {

    private final SkillManager skillManager;

    public SkillCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendSkillList(player);
            return true;
        }

        SkillManager.SkillType skill;
        try {
            skill = SkillManager.SkillType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown skill: " + args[0] + ". Use /skill to see all skills.");
            return true;
        }

        sendSkillDetail(player, skill);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(SkillManager.SkillType.values())
                    .map(s -> s.name().toLowerCase())
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendSkillList(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Your Skills ===");
        for (SkillManager.SkillType skill : SkillManager.SkillType.values()) {
            int level = skillManager.getLevel(id, skill);
            player.sendMessage(skill.getDisplayName() + ": Level " + level + "/" + SkillManager.MAX_LEVEL);
        }
    }

    private void sendSkillDetail(Player player, SkillManager.SkillType skill) {
        UUID id = player.getUniqueId();
        int level = skillManager.getLevel(id, skill);
        long xp = skillManager.getXp(id, skill);
        SkillLevelManager levelManager = SkillLevelManager.getInstance();
        player.sendMessage("=== " + skill.getDisplayName() + " ===");
        player.sendMessage("Level: " + level + "/" + SkillManager.MAX_LEVEL);
        player.sendMessage("Total XP: " + xp);
        long toNext = levelManager.xpToNextLevel(xp);
        if (toNext > 0) {
            player.sendMessage("XP to next level: " + toNext);
        } else {
            player.sendMessage("Max level reached!");
        }
    }
}
