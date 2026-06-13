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
 * Handles the {@code /skills} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /skills}              — show all skill levels</li>
 *   <li>{@code /skills <skill>}      — show level and XP for a specific skill</li>
 *   <li>{@code /skills info}         — list all available skills</li>
 *   <li>{@code /skills reset}        — reset all skill data</li>
 * </ul>
 * </p>
 */
public final class SkillsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS;

    static {
        List<String> subs = new java.util.ArrayList<>(Arrays.asList("info", "reset"));
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            subs.add(skill.name().toLowerCase());
        }
        SUBCOMMANDS = Collections.unmodifiableList(subs);
    }

    private final SkillsManager skillsManager;

    public SkillsCommand(SkillsManager skillsManager) {
        if (skillsManager == null) {
            throw new IllegalArgumentException("skillsManager must not be null");
        }
        this.skillsManager = skillsManager;
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
            case "info"  -> handleInfo(player);
            case "reset" -> handleReset(player);
            default      -> handleSkill(player, args[0]);
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
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            int level = skillsManager.getLevel(id, skill);
            double xp = skillsManager.getXp(id, skill);
            player.sendMessage(String.format("  %-14s Level %d  (%.1f XP)",
                    skill.getDisplayName(), level, xp));
        }
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Available Skills ===");
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            player.sendMessage("  " + skill.getDisplayName());
        }
    }

    private void handleReset(Player player) {
        player.sendMessage("Skill reset is not currently supported.");
    }

    private void handleSkill(Player player, String name) {
        SkillsManager.SkillType skill = parseSkill(name);
        if (skill == null) {
            player.sendMessage("Unknown skill: " + name);
            sendHelp(player);
            return;
        }
        UUID id = player.getUniqueId();
        int level = skillsManager.getLevel(id, skill);
        double xp = skillsManager.getXp(id, skill);
        player.sendMessage("=== " + skill.getDisplayName() + " ===");
        player.sendMessage("  Level: " + level);
        player.sendMessage("  XP   : " + String.format("%.1f", xp));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Skills Commands ===");
        player.sendMessage("/skills                  — show all skill levels");
        player.sendMessage("/skills <skill>          — show level and XP for a skill");
        player.sendMessage("/skills info             — list all available skills");
    }

    private static SkillsManager.SkillType parseSkill(String name) {
        for (SkillsManager.SkillType skill : SkillsManager.SkillType.values()) {
            if (skill.name().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }
}
