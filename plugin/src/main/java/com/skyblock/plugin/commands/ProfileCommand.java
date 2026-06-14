package com.skyblock.plugin.commands;

import com.skyblock.core.profile.ProfileManager;
import com.skyblock.plugin.managers.DungeonManager;
import com.skyblock.plugin.managers.SkillsManager;
import com.skyblock.plugin.managers.SlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("skills")) {
            handleSkills(player);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
            handleStats(player);
            return true;
        }

        UUID id = player.getUniqueId();
        ProfileManager manager = ProfileManager.getInstance();
        List<ProfileManager.SkyBlockProfile> profiles = manager.getProfilesForOwner(id);
        player.sendMessage("=== Profile ===");
        player.sendMessage("Fairy Souls: " + manager.getFairySouls(id));
        player.sendMessage("SkyBlock XP: " + manager.getSkyBlockXp(id));
        player.sendMessage("Profiles (" + profiles.size() + "):");
        for (ProfileManager.SkyBlockProfile profile : profiles) {
            player.sendMessage("  " + profile.name() + " — " + profile.gameMode().getDisplayName());
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();

        SkillsManager skills = SkillsManager.getInstance();
        Map<String, Long> xpMap = skills.getSkillXPs(id);
        player.sendMessage("=== Stats ===");
        player.sendMessage("-- Skills --");
        for (Map.Entry<String, long[]> entry : SkillsManager.SKILL_XP_TABLE.entrySet()) {
            String skill = entry.getKey();
            long[] table = entry.getValue();
            long totalXP = xpMap.getOrDefault(skill, 0L);
            long cumulative = 0;
            int level = 0;
            for (long threshold : table) {
                cumulative += threshold;
                if (totalXP < cumulative) break;
                level++;
            }
            player.sendMessage(skill + ": level " + level + " (" + totalXP + " XP)");
        }

        SlayerManager slayer = SlayerManager.getInstance();
        Map<String, Long> slayerXp = slayer.getSlayerXp(id);
        player.sendMessage("-- Slayer --");
        if (slayerXp.isEmpty()) {
            player.sendMessage("No slayer XP yet.");
        } else {
            for (Map.Entry<String, Long> entry : slayerXp.entrySet()) {
                long kills = slayer.getKillCount(id, entry.getKey());
                player.sendMessage(entry.getKey() + ": " + entry.getValue() + " XP, " + kills + " kills");
            }
        }

        DungeonManager dungeons = DungeonManager.getInstance();
        player.sendMessage("-- Dungeons --");
        String cls = dungeons.getPlayerClass(id);
        player.sendMessage("Class: " + (cls.isEmpty() ? "None" : cls));
        player.sendMessage("Highest Floor: " + dungeons.getHighestFloor(id));
        Map<String, Integer> completions = dungeons.getFloorCompletions(id);
        if (!completions.isEmpty()) {
            for (Map.Entry<String, Integer> entry : completions.entrySet()) {
                player.sendMessage("  " + entry.getKey() + ": " + entry.getValue() + " completions");
            }
        }
    }

    private void handleSkills(Player player) {
        UUID id = player.getUniqueId();
        SkillsManager manager = SkillsManager.getInstance();
        Map<String, Long> xpMap = manager.getSkillXPs(id);
        player.sendMessage("=== Skills ===");
        for (String skill : SkillsManager.SKILL_XP_TABLE.keySet()) {
            long xp = xpMap.getOrDefault(skill, 0L);
            player.sendMessage(skill + ": " + xp + " XP");
        }
    }
}
