package com.skyblock.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Base for commands that handle any sender.
 * Subclasses implement {@link #execute(CommandSender, Command, String, String[])} instead of
 * {@code onCommand}; the command is always considered handled.
 */
public abstract class BaseCommand implements CommandExecutor {

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, command, label, args);
        return true;
    }

    protected abstract void execute(CommandSender sender, Command command, String label, String[] args);
}
