package com.skyblock.core.command;

import com.skyblock.core.manager.ProfileManager;
import com.skyblock.core.manager.ProfileManager.SkyBlockProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ProfileCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("create", "delete", "view", "list");

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
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "view"   -> handleView(player, args);
            case "list"   -> handleList(player);
            default       -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("view"))) {
            if (!(sender instanceof Player player)) {
                return Collections.emptyList();
            }
            String prefix = args[1].toLowerCase();
            return ProfileManager.getInstance().getProfilesForOwner(player.getUniqueId()).stream()
                    .map(SkyBlockProfile::name)
                    .filter(s -> s.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile create <name>");
            return;
        }
        String name = args[1];
        SkyBlockProfile created = ProfileManager.getInstance()
                .createProfile(player.getUniqueId(), name, ProfileManager.GameMode.NORMAL);
        if (created != null) {
            player.sendMessage("Profile '" + name + "' created.");
        } else {
            player.sendMessage("Could not create profile '" + name + "' (limit reached or duplicate).");
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile delete <name>");
            return;
        }
        String name = args[1];
        UUID uuid = player.getUniqueId();
        SkyBlockProfile target = ProfileManager.getInstance().getProfilesForOwner(uuid).stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (target != null && ProfileManager.getInstance().deleteProfile(target.profileId())) {
            player.sendMessage("Profile '" + name + "' deleted.");
        } else {
            player.sendMessage("No profile named '" + name + "' found.");
        }
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile view <name>");
            return;
        }
        String name = args[1];
        SkyBlockProfile profile = ProfileManager.getInstance().getProfilesForOwner(player.getUniqueId()).stream()
                .filter(p -> p.name().equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (profile == null) {
            player.sendMessage("No profile named '" + name + "' found.");
            return;
        }
        player.sendMessage("=== Profile: " + profile.name() + " ===");
        player.sendMessage("  Game Mode: " + profile.gameMode().getDisplayName());
    }

    private void handleList(Player player) {
        List<SkyBlockProfile> profiles = ProfileManager.getInstance().getProfilesForOwner(player.getUniqueId());
        if (profiles.isEmpty()) {
            player.sendMessage("You have no profiles. Use /profile create <name> to make one.");
            return;
        }
        player.sendMessage("=== Your Profiles ===");
        for (SkyBlockProfile profile : profiles) {
            player.sendMessage("  " + profile.name() + " [" + profile.gameMode().getDisplayName() + "]");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Profile Commands ===");
        player.sendMessage("/profile create <name> — create a new profile");
        player.sendMessage("/profile delete <name> — delete a profile");
        player.sendMessage("/profile view <name> — view profile details");
        player.sendMessage("/profile list — list all your profiles");
    }
}
