package com.skyblock.core.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SkillsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "xp", "level");

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

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"  -> handleView(player);
            case "xp"    -> handleXp(player, args);
            case "level" -> handleLevel(player, args);
            default      -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("xp") || args[0].equalsIgnoreCase("level"))) {
            String prefix = args[1].toLowerCase();
            return SkillsManager.SKILLS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleView(Player player) {
        Map<String, Integer> levels = manager.getSkillLevels(player.getUniqueId());
        player.sendMessage("=== Skills ===");
        for (String skill : SkillsManager.SKILLS) {
            int level = levels.getOrDefault(skill, 0);
            player.sendMessage("  " + skill + ": level " + level);
        }
    }

    private void handleXp(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skills xp <skill>");
            return;
        }
        String skill = args[1].toLowerCase();
        if (!SkillsManager.SKILLS.contains(skill)) {
            player.sendMessage("Unknown skill '" + skill + "'.");
            return;
        }
        long xp = manager.getXp(player.getUniqueId(), skill);
        int level = manager.getLevel(player.getUniqueId(), skill);
        player.sendMessage("=== " + skill + " ===");
        player.sendMessage("  Level: " + level);
        player.sendMessage("  XP: " + xp);
    }

    private void handleLevel(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skills level <skill>");
            return;
        }
        String skill = args[1].toLowerCase();
        if (!SkillsManager.SKILLS.contains(skill)) {
            player.sendMessage("Unknown skill '" + skill + "'.");
            return;
        }
        int level = manager.getLevel(player.getUniqueId(), skill);
        player.sendMessage("Your " + skill + " level is " + level + ".");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Skills Commands ===");
        player.sendMessage("/skills view — view all your skill levels");
        player.sendMessage("/skills xp <skill> — view XP and level for a skill");
        player.sendMessage("/skills level <skill> — view your level in a skill");
    }
}
