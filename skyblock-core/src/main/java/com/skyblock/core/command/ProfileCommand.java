package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.ProfileMenu;
import com.skyblock.core.profile.manager.ProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles {@code /profile [subcommand]}: opens the ProfileMenu or manages profile slots.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /profile}                  — open the profile menu</li>
 *   <li>{@code /profile list}             — list your profile slots</li>
 *   <li>{@code /profile create <name>}    — create a new profile slot (up to {@value ProfileManager#MAX_PROFILES})</li>
 *   <li>{@code /profile switch <index>}   — switch to a profile slot by 1-based index</li>
 *   <li>{@code /profile delete <index>}   — delete a profile slot by 1-based index</li>
 * </ul>
 * </p>
 */
public final class ProfileCommand extends PlayerCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "create", "switch", "delete");

    private final ProfileManager profileManager;

    public ProfileCommand(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    protected void openMenu(Player p) {
        new ProfileMenu(SkyBlockCore.getInstance(), p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "create" -> handleCreate(player, args);
            case "switch" -> handleSwitch(player, args);
            case "delete" -> handleDelete(player, args);
            default       -> player.sendMessage("Unknown subcommand. Usage: /profile <list|create|switch|delete>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleList(Player player) {
        List<ProfileManager.SkyBlockProfile> profiles = profileManager.getProfilesForOwner(player.getUniqueId());
        ProfileManager.SkyBlockProfile active = profileManager.getActiveProfile(player.getUniqueId());
        player.sendMessage("=== Your Profile Slots (" + profiles.size() + "/" + ProfileManager.MAX_PROFILES + ") ===");
        if (profiles.isEmpty()) {
            player.sendMessage("No profiles. Use /profile create <name> to create one.");
            return;
        }
        for (int i = 0; i < profiles.size(); i++) {
            ProfileManager.SkyBlockProfile p = profiles.get(i);
            boolean isActive = active != null && p.profileId().equals(active.profileId());
            player.sendMessage((i + 1) + ". " + p.name() + " [" + p.gameMode().getDisplayName() + "]"
                    + (isActive ? " §a(active)" : ""));
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile create <name>");
            return;
        }
        String name = args[1];
        ProfileManager.SkyBlockProfile profile = profileManager.createProfile(
                player.getUniqueId(), name, ProfileManager.GameMode.NORMAL);
        if (profile == null) {
            player.sendMessage("§cYou have reached the maximum of " + ProfileManager.MAX_PROFILES + " profile slots.");
            return;
        }
        player.sendMessage("§aCreated profile \"" + name + "\".");
    }

    private void handleSwitch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile switch <index>");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cIndex must be a number.");
            return;
        }
        ProfileManager.SkyBlockProfile profile = profileManager.switchProfile(player.getUniqueId(), index);
        if (profile == null) {
            player.sendMessage("§cNo profile at index " + index + ". Use /profile list to see your slots.");
            return;
        }
        player.sendMessage("§aSwitched to profile \"" + profile.name() + "\".");
    }

    private void handleDelete(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /profile delete <index>");
            return;
        }
        int index;
        try {
            index = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cIndex must be a number.");
            return;
        }
        List<ProfileManager.SkyBlockProfile> profiles = profileManager.getProfilesForOwner(player.getUniqueId());
        if (index < 1 || index > profiles.size()) {
            player.sendMessage("§cNo profile at index " + index + ". Use /profile list to see your slots.");
            return;
        }
        ProfileManager.SkyBlockProfile toDelete = profiles.get(index - 1);
        profileManager.deleteProfile(toDelete.profileId());
        player.sendMessage("§aDeleted profile \"" + toDelete.name() + "\".");
    }
}
