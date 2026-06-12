package com.skyblock.core.command;

import com.skyblock.core.profile.ProfileManager;
import com.skyblock.core.profile.ProfileManager.SkyBlockProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /profile} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /profile}                       — list your profiles</li>
 *   <li>{@code /profile create <name>}          — create a new profile</li>
 *   <li>{@code /profile delete <name>}          — delete one of your profiles</li>
 * </ul>
 * </p>
 */
public final class ProfileCommand implements TabExecutor {

    private final ProfileManager profileManager;

    public ProfileCommand(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendProfileList(player);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "create" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /profile create <name>");
                    return true;
                }
                String name = args[1];
                SkyBlockProfile created = profileManager.createProfile(player.getUniqueId(), name);
                sender.sendMessage("Profile \"" + created.name() + "\" created with id " + created.profileId() + ".");
            }
            case "delete" -> {
                if (args.length < 2) {
                    sender.sendMessage("Usage: /profile delete <name>");
                    return true;
                }
                String name = args[1];
                SkyBlockProfile target = findByName(player, name);
                if (target == null) {
                    sender.sendMessage("You do not have a profile named \"" + name + "\".");
                    return true;
                }
                profileManager.deleteProfile(target.profileId());
                sender.sendMessage("Profile \"" + target.name() + "\" deleted.");
            }
            default -> sender.sendMessage("Unknown sub-command. Use /profile [create|delete].");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("create", "delete").stream()
                    .filter(s -> s.startsWith(lower))
                    .collect(java.util.stream.Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendProfileList(Player player) {
        List<SkyBlockProfile> profiles = profileManager.getProfilesForOwner(player.getUniqueId());
        if (profiles.isEmpty()) {
            player.sendMessage("You have no profiles. Use /profile create <name> to make one.");
            return;
        }
        player.sendMessage("=== Your Profiles ===");
        for (SkyBlockProfile p : profiles) {
            player.sendMessage("- " + p.name() + " (" + p.profileId() + ")");
        }
    }

    private SkyBlockProfile findByName(Player player, String name) {
        for (SkyBlockProfile p : profileManager.getProfilesForOwner(player.getUniqueId())) {
            if (p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
