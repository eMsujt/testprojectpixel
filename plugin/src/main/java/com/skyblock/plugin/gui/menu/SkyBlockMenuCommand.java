package com.skyblock.plugin.gui.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @deprecated Use {@link com.skyblock.plugin.command.menu.SkyblockMenuCommand} instead.
 */
@Deprecated
public final class SkyBlockMenuCommand implements CommandExecutor {
    private final com.skyblock.plugin.command.menu.SkyblockMenuCommand delegate = new com.skyblock.plugin.command.menu.SkyblockMenuCommand();
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
}
