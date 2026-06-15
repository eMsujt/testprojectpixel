package com.skyblock.core.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @deprecated Use {@link com.skyblock.core.command.SkyblockMenuCommand} instead.
 */
@Deprecated
public final class SkyblockMenuCommand implements CommandExecutor {
    private final com.skyblock.core.command.SkyblockMenuCommand delegate = new com.skyblock.core.command.SkyblockMenuCommand();
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
}
