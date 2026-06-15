package com.skyblock.core.command;

import com.skyblock.core.profile.ProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProfileCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("create", "delete", "view", "list");

    private final ProfileManager manager;

    public ProfileCommand(ProfileManager manager) {
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
            return manager.getProfileNames(player.getUniqueId()).stream()
                    .filter(s -> s.startsWith(prefix))
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
        if (manager.createProfile(player.getUniqueId(), name)) {
            player.sendMessage("Profile '" + name + "' created.");
        } else {
            player.sendMessage("A profile named '" + name + "' already exists.");
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile delete <name>");
            return;
        }
        String name = args[1];
        if (manager.deleteProfile(player.getUniqueId(), name)) {
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
        ProfileManager.ProfileData data = manager.getProfile(player.getUniqueId(), name);
        if (data == null) {
            player.sendMessage("No profile named '" + name + "' found.");
            return;
        }
        player.sendMessage("=== Profile: " + data.getName() + " ===");
        player.sendMessage("  Game Mode: " + data.getGameMode());
        player.sendMessage("  Coins: " + data.getCoinsBalance());
    }

    private void handleList(Player player) {
        Map<String, ProfileManager.ProfileData> profiles = manager.getProfiles(player.getUniqueId());
        if (profiles.isEmpty()) {
            player.sendMessage("You have no profiles. Use /profile create <name> to make one.");
            return;
        }
        player.sendMessage("=== Your Profiles ===");
        for (Map.Entry<String, ProfileManager.ProfileData> entry : profiles.entrySet()) {
            player.sendMessage("  " + entry.getValue().getName() + " [" + entry.getValue().getGameMode() + "]");
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
