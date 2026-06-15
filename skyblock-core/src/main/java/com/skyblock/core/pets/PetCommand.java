package com.skyblock.core.pets;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.pets.command.PetCommand} instead.
 */
@Deprecated
public final class PetCommand implements TabExecutor {
    private final com.skyblock.core.pets.command.PetCommand delegate;
    public PetCommand(PetManager petManager) {
        this.delegate = new com.skyblock.core.pets.command.PetCommand(petManager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
