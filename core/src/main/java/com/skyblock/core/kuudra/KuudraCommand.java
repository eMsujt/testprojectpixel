package com.skyblock.core.kuudra;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.command.KuudraCommand} instead.
 */
@Deprecated
public final class KuudraCommand implements TabExecutor {
    private final com.skyblock.core.command.KuudraCommand delegate;
    public KuudraCommand(KuudraManager manager) {
        this.delegate = new com.skyblock.core.command.KuudraCommand(manager);
    }
    @Override public boolean onCommand(CommandSender s, Command c, String l, String[] a) { return delegate.onCommand(s, c, l, a); }
    @Override public List<String> onTabComplete(CommandSender s, Command c, String al, String[] a) { return delegate.onTabComplete(s, c, al, a); }
}
