package com.skyblock.core.command;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Generic command that simply opens a menu for the invoking player.
 *
 * <p>Consolidates the trivial single-method menu-opener commands (e.g. /pets,
 * /stats, /trophyfishing) that did nothing but construct a menu and open it.
 * Register one with the menu's open action, e.g.
 * {@code new MenuCommand(p -> new PetMenu(p).open(p))}.</p>
 */
public final class MenuCommand extends PlayerCommand {

    private final Consumer<Player> opener;

    public MenuCommand(Consumer<Player> opener) {
        this.opener = opener;
    }

    @Override
    protected void openMenu(Player p) {
        opener.accept(p);
    }
}
