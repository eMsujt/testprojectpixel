package com.skyblock.core.menu;

import org.bukkit.entity.Player;

/**
 * Player-aware abstract menu base for menus that do not require a plugin reference.
 *
 * <p>Extends {@link Menu} with a protected {@code player} field. Subclasses
 * implement {@link #populate()} to fill slots via
 * {@link Menu#setItem(int, org.bukkit.inventory.ItemStack)}.</p>
 */
public abstract class AbstractSkyBlockMenu extends Menu {

    protected final Player player;

    protected AbstractSkyBlockMenu(Player player, String title, int rows) {
        super(title, rows);
        this.player = player;
    }

    protected abstract void populate();

    @Override
    protected final void build() {
        populate();
    }
}
