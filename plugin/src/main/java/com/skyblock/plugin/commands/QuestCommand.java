package com.skyblock.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/** @deprecated Use {@link SkyblockQuestCommand} instead. */
@Deprecated
public final class QuestCommand implements CommandExecutor {

    private final SkyblockQuestCommand delegate = new SkyblockQuestCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return delegate.onCommand(sender, command, label, args);
    }
}
