package com.skyblock.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

/**
 * @deprecated Use {@code com.skyblock.core.command.SkyblockMenuCommand} from skyblock-core,
 *             which opens {@link com.skyblock.core.menu.SkyBlockMainMenu} via
 *             {@link com.skyblock.core.menu.SkyBlockMenuManager}.
 */
@Deprecated
public final class SkyblockMenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("This command is deprecated. Use /skyblock instead.");
        return true;
    }
}
