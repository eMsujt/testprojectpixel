package com.skyblock.core.pets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.pets.command.PetsCommand} instead.
 */
@Deprecated
public final class PetsCommand implements TabExecutor {
    private final com.skyblock.core.pets.command.PetsCommand delegate;
    public PetsCommand(PetsManager petsManager) {
        this.delegate = new com.skyblock.core.pets.command.PetsCommand(petsManager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
