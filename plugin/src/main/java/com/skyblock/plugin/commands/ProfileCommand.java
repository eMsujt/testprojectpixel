package com.skyblock.plugin.commands;

import com.skyblock.core.profile.ProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class ProfileCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
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
}
