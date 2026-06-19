package com.skyblock.core.command;

import com.skyblock.core.profile.manager.ProfileManager;
import com.skyblock.core.profile.manager.ProfileManager.SkyBlockProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Handles {@code /profile}: with no arguments it opens the profile menu; with a
 * number {@code /profile [1-4]} it switches the player to that profile.
 */
public final class ProfileCommand extends PlayerCommand {

    private final ProfileManager profileManager;
    private final Consumer<Player> menuOpener;

    public ProfileCommand(ProfileManager profileManager, Consumer<Player> menuOpener) {
        this.profileManager = profileManager;
        this.menuOpener = menuOpener;
    }

    @Override
    protected void openMenu(Player p) {
        menuOpener.accept(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            menuOpener.accept(player);
            return true;
        }
        int index;
        try {
            index = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Usage: /profile [number]");
            return true;
        }
        SkyBlockProfile switched = profileManager.switchProfile(player.getUniqueId(), index);
        if (switched == null) {
            player.sendMessage(ChatColor.RED + "You have no profile #" + index + ".");
            return true;
        }
        player.sendMessage(ChatColor.GREEN + "Switched to profile " + ChatColor.YELLOW + switched.name() + ChatColor.GREEN + ".");
        return true;
    }
}
