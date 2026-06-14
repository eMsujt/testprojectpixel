package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.SkillsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class SkillsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStats(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats" -> handleStats(player);
            case "info"  -> handleInfo(player, args);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        SkillsManager manager = SkillsManager.getInstance();
        Map<String, Long> xpMap = manager.getSkillXPs(id);

        player.sendMessage("=== Skill Stats ===");
        for (String skill : SkillsManager.SKILL_XP_TABLE.keySet()) {
            long xp = xpMap.getOrDefault(skill, 0L);
            int level = computeLevel(skill, xp);
            player.sendMessage(capitalize(skill) + " — Level: " + level + ", XP: " + xp);
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skills info <skill>");
            return;
        }
        String skill = args[1].toLowerCase();
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        if (table == null) {
            player.sendMessage("Unknown skill: " + args[1]
                    + ". Use /skills stats to list valid skills.");
            return;
        }

        UUID id = player.getUniqueId();
        long xp = SkillsManager.getInstance().getSkillXP(id, skill);
        int level = computeLevel(skill, xp);
        long nextThreshold = level < table.length ? table[level] : -1L;

        player.sendMessage("=== " + capitalize(skill) + " ===");
        player.sendMessage("Level: " + level + " / " + table.length);
        player.sendMessage("Total XP: " + xp);
        if (nextThreshold >= 0) {
            player.sendMessage("XP to next level: " + Math.max(0L, nextThreshold - xp));
        } else {
            player.sendMessage("Max level reached.");
        }
    }

    private int computeLevel(String skill, long xp) {
        long[] table = SkillsManager.SKILL_XP_TABLE.get(skill);
        if (table == null) return 0;
        int level = 0;
        long cumulative = 0;
        for (long threshold : table) {
            cumulative += threshold;
            if (xp >= cumulative) {
                level++;
            } else {
                break;
            }
        }
        return level;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Skills Commands ===");
        player.sendMessage("/skills stats        — show your level and XP for all skills");
        player.sendMessage("/skills info <skill> — show detailed info for a specific skill");
    }
}
