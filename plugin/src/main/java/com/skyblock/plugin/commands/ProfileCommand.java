package com.skyblock.plugin.commands;

import com.skyblock.core.profile.ProfileManager;
import com.skyblock.plugin.managers.SkillsManager;
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
