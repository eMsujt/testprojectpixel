package com.skyblock.core.command;

import com.skyblock.core.menu.SkyBlockMainMenu;
import com.skyblock.core.menu.SkyBlockMenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /skyblock} (alias {@code /sb}) command.
 *
 * <p>Opens the SkyBlock main menu GUI for the player, with shortcuts to
 * key features: Skills, Pets, Quests, Collections, Minions, Slayer,
 * Bazaar, and Auction House.</p>
 */
/**
 * @deprecated Use {@link com.skyblock.core.hub.SkyblockHubCommand} instead.
 */
@Deprecated
public final class SkyBlockMenuCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        SkyBlockMenuManager.getInstance().openMainMenu(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
