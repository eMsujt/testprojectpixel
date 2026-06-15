package com.skyblock.core.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

/**
 * @deprecated Moved to {@link com.skyblock.core.command.SkyblockMenuCommand}.
 *             Use {@link com.skyblock.core.hub.SkyblockHubCommand} for the full
 *             {@code /skyblock} router.
 */
@Deprecated
public final class SkyBlockCommand implements TabExecutor {

    public SkyBlockCommand(SkyBlockMenuManager menuManager) {
        // deprecated — menuManager arg kept for binary compatibility
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("This command is deprecated. Use /skyblock instead.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
