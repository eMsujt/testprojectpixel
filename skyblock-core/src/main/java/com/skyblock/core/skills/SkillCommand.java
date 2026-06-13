package com.skyblock.core.skills;

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
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /skill}              — show all skill levels</li>
 *   <li>{@code /skill <skill>}      — show level and XP for a specific skill</li>
 *   <li>{@code /skill info}         — list all available skills</li>
 * </ul>
 * </p>
 */
public final class SkillCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS;

    static {
        List<String> subs = new java.util.ArrayList<>(Arrays.asList("info"));
        for (SkillManager.SkillType skill : SkillManager.SkillType.values()) {
            subs.add(skill.name().toLowerCase());
        }
        SUBCOMMANDS = Collections.unmodifiableList(subs);
    }

    private final SkillManager skillManager;

    public SkillCommand(SkillManager skillManager) {
        if (skillManager == null) {
            throw new IllegalArgumentException("skillManager must not be null");
        }
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleAll(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info" -> handleInfo(player);
            default     -> handleSkill(player, args[0]);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleAll(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Skills ===");
        for (SkillManager.SkillType skill : SkillManager.SkillType.values()) {
            int level = skillManager.getLevel(id, skill);
            double xp = skillManager.getXp(id, skill);
            player.sendMessage(String.format("  %-14s Level %d  (%.1f XP)",
                    skill.getDisplayName(), level, xp));
        }
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Available Skills ===");
        for (SkillManager.SkillType skill : SkillManager.SkillType.values()) {
            player.sendMessage("  " + skill.getDisplayName());
        }
    }

    private void handleSkill(Player player, String name) {
        SkillManager.SkillType skill = parseSkill(name);
        if (skill == null) {
            player.sendMessage("Unknown skill: " + name);
            sendHelp(player);
            return;
        }
        UUID id = player.getUniqueId();
        int level = skillManager.getLevel(id, skill);
        double xp = skillManager.getXp(id, skill);
        player.sendMessage("=== " + skill.getDisplayName() + " ===");
        player.sendMessage("  Level: " + level);
        player.sendMessage("  XP   : " + String.format("%.1f", xp));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Skill Commands ===");
        player.sendMessage("/skill                   — show all skill levels");
        player.sendMessage("/skill <skill>           — show level and XP for a skill");
        player.sendMessage("/skill info              — list all available skills");
    }

    private static SkillManager.SkillType parseSkill(String name) {
        for (SkillManager.SkillType skill : SkillManager.SkillType.values()) {
            if (skill.name().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }
}
