package com.skyblock.core.command;

import com.skyblock.core.skills.SkillManager;
import com.skyblock.core.skills.SkillManager.SkillType;
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
 * Handles the {@code /skills} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /skills}         — lists all skill levels for the sender</li>
 *   <li>{@code /skills <skill>} — shows XP details for a specific skill</li>
 * </ul>
 * </p>
 */
public final class SkillsCommand implements TabExecutor {

    private final SkillManager skillManager;

    public SkillsCommand(SkillManager skillManager) {
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

        String input = args[0].toUpperCase();
        SkillType skill;
        try {
            skill = SkillType.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown skill: " + args[0] + ". Use /skills to see all skills.");
            return true;
        }

        sendSkillDetail(player, skill);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toUpperCase();
            return Arrays.stream(SkillType.values())
                    .map(s -> s.name().toLowerCase())
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendSkillList(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Your Skills ===");
        for (SkillType skill : SkillType.values()) {
            int level = skillManager.getLevel(id, skill);
            player.sendMessage(capitalize(skill.name()) + ": Level " + level + "/" + SkillManager.MAX_LEVEL);
        }
    }

    private void sendSkillDetail(Player player, SkillType skill) {
        UUID id = player.getUniqueId();
        int level = skillManager.getLevel(id, skill);
        double xp = skillManager.getXp(id, skill);
        player.sendMessage("=== " + capitalize(skill.name()) + " ===");
        player.sendMessage("Level: " + level + "/" + SkillManager.MAX_LEVEL);
        player.sendMessage("Total XP: " + (long) xp);
        if (level < SkillManager.MAX_LEVEL) {
            double nextThreshold = 50.0 * (level + 1) * (level + 1);
            player.sendMessage("XP to next level: " + (long) (nextThreshold - xp));
        } else {
            player.sendMessage("Max level reached!");
        }
    }

    private static String capitalize(String name) {
        if (name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }
}
