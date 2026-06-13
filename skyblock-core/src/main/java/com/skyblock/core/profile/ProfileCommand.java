package com.skyblock.core.profile;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /profile} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /profile list}                      — list your profiles</li>
 *   <li>{@code /profile create <name> [gamemode]}  — create a new profile (max 4)</li>
 *   <li>{@code /profile delete <name>}             — delete a profile by name</li>
 *   <li>{@code /profile mode}                      — list all available profile modes</li>
 * </ul>
 * </p>
 */
public final class ProfileCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "create", "delete", "mode", "data");
    private static final List<String> GAME_MODES = Arrays.asList("normal", "ironman", "bingo");
    private static final List<String> PROFILE_MODES = Arrays.asList("classic", "ironman", "stranded", "bingo");

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
            player.sendMessage("Usage: /profile <list|create|delete|mode|data>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "mode"   -> handleMode(player);
            case "data"   -> handleData(player);
            default       -> player.sendMessage("Unknown subcommand. Usage: /profile <list|create|delete|mode|data>");
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
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            String prefix = args[2].toLowerCase();
            return GAME_MODES.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("mode")) {
            String prefix = args[1].toLowerCase();
            return PROFILE_MODES.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<ProfileManager.SkyBlockProfile> profiles = profileManager.getProfilesForOwner(player.getUniqueId());
        if (profiles.isEmpty()) {
            player.sendMessage("You have no profiles. Use /profile create <name> to make one.");
            return;
        }
        player.sendMessage("=== Your Profiles (" + profiles.size() + "/" + ProfileManager.MAX_PROFILES + ") ===");
        for (ProfileManager.SkyBlockProfile p : profiles) {
            player.sendMessage("  " + p.name() + " [" + p.gameMode().getDisplayName() + "]"
                    + "  (id: " + p.profileId() + ")");
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile create <name> [normal|ironman|bingo]");
            return;
        }
        String name = args[1];
        ProfileManager.GameMode gameMode = ProfileManager.GameMode.NORMAL;
        if (args.length >= 3) {
            try {
                gameMode = ProfileManager.GameMode.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown game mode \"" + args[2] + "\". Valid options: normal, ironman, bingo.");
                return;
            }
        }
        ProfileManager.SkyBlockProfile profile = profileManager.createProfile(player.getUniqueId(), name, gameMode);
        if (profile == null) {
            player.sendMessage("You have reached the maximum of " + ProfileManager.MAX_PROFILES + " profiles.");
        } else {
            player.sendMessage("Profile \"" + profile.name() + "\" (" + profile.gameMode().getDisplayName()
                    + ") created successfully!");
        }
    }

    private void handleMode(Player player) {
        player.sendMessage("=== Profile Modes ===");
        ProfileManager.ProfileMode[] modes = ProfileManager.ProfileMode.values();
        for (int i = 0; i < modes.length; i++) {
            player.sendMessage(String.format("%d. %s", i + 1, modes[i].getDisplayName()));
        }
    }

    private void handleData(Player player) {
        ProfileManager.ProfileData data = profileManager.getPlayerData(player.getUniqueId());
        if (data == null) {
            player.sendMessage("No profile data found for your account.");
            return;
        }
        player.sendMessage("=== Profile Data ===");
        player.sendMessage("  Name      : " + data.profileName());
        player.sendMessage("  Created   : " + data.createdAt());
        if (data.stats().isEmpty()) {
            player.sendMessage("  Stats     : (none)");
        } else {
            player.sendMessage("  Stats:");
            for (java.util.Map.Entry<String, Double> e : data.stats().entrySet()) {
                player.sendMessage("    " + e.getKey() + ": " + e.getValue());
            }
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile delete <name>");
            return;
        }
        String name = args[1];
        List<ProfileManager.SkyBlockProfile> profiles = profileManager.getProfilesForOwner(player.getUniqueId());
        ProfileManager.SkyBlockProfile target = profiles.stream()
                .filter(p -> p.name().equals(name))
                .findFirst()
                .orElse(null);
        if (target == null) {
            player.sendMessage("No profile named \"" + name + "\" found.");
            return;
        }
        profileManager.deleteProfile(target.profileId());
        player.sendMessage("Profile \"" + name + "\" has been deleted.");
    }
}
