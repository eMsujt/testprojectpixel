package com.skyblock.core.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>Opens the SkyBlock main menu GUI for the executing player via
 * {@link SkyBlockMenuManager}.</p>
 */
public final class SkyBlockCommand implements TabExecutor {

    private final SkyBlockMenuManager menuManager;

    public SkyBlockCommand(SkyBlockMenuManager menuManager) {
        this.menuManager = Objects.requireNonNull(menuManager, "menuManager");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        menuManager.openMainMenu(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
