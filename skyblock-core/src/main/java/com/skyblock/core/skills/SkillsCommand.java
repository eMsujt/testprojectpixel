package com.skyblock.core.skills;

import com.skyblock.core.manager.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SkillsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("stats", "info", "top");

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
            handleStats(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats"       -> handleStats(player);
            case "info"        -> handleInfo(player, args);
            case "top"         -> handleTop(player, args);
            default            -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("top"))) {
            return SkillManager.SKILL_XP_TABLE.keySet().stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Long> xpMap = skillManager.getSkillXPs(id);

        player.sendMessage("=== Skill Stats ===");
        for (String skill : SkillManager.SKILL_XP_TABLE.keySet()) {
            long xp = xpMap.getOrDefault(skill, 0L);
            int level = SkillManager.levelForXp(skill, xp);
            player.sendMessage(capitalize(skill) + " — Level: " + level + ", XP: " + xp);
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skills info <skill>");
            return;
        }
        String skill = args[1].toLowerCase();
        long[] table = SkillManager.SKILL_XP_TABLE.get(skill);
        if (table == null) {
            player.sendMessage("Unknown skill: " + args[1]
                    + ". Use /skills stats to list valid skills.");
            return;
        }

        UUID id = player.getUniqueId();
        long xp = skillManager.getSkillXP(id, skill);
        int level = SkillManager.levelForXp(skill, xp);
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

    private void handleTop(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skills top <skill>");
            return;
        }
        String skill = args[1].toLowerCase();
        if (!SkillManager.SKILL_XP_TABLE.containsKey(skill)) {
            player.sendMessage("Unknown skill: " + args[1]
                    + ". Use /skills stats to list valid skills.");
            return;
        }

        Map<UUID, Long> allXP = skillManager.getAllSkillXP(skill);
        List<Map.Entry<UUID, Long>> sorted = new ArrayList<>(allXP.entrySet());
        sorted.sort(Comparator.comparingLong(Map.Entry<UUID, Long>::getValue).reversed());

        player.sendMessage("=== Top " + capitalize(skill) + " Players ===");
        int rank = 1;
        for (Map.Entry<UUID, Long> entry : sorted) {
            player.sendMessage(rank + ". " + entry.getKey()
                    + " — XP: " + entry.getValue()
                    + ", Level: " + SkillManager.levelForXp(skill, entry.getValue()));
            if (++rank > 10) break;
        }
        if (sorted.isEmpty()) {
            player.sendMessage("No data found.");
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Skills Commands ===");
        player.sendMessage("/skills stats        — show your level and XP for all skills");
        player.sendMessage("/skills info <skill> — show detailed info for a specific skill");
        player.sendMessage("/skills top <skill>  — show top 10 players by XP for a skill");
    }
}
