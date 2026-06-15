package com.skyblock.core.dungeon;

import com.skyblock.core.manager.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.command.DungeonCommand} instead.
 */
@Deprecated
public final class DungeonCommand implements TabExecutor {
    private final com.skyblock.core.command.DungeonCommand delegate;
    public DungeonCommand(DungeonManager manager) {
        this.delegate = new com.skyblock.core.command.DungeonCommand(manager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
