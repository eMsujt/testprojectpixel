package com.skyblock.core.menu;

import com.skyblock.core.SkyblockPlugin;
import org.bukkit.entity.Player;

/**
 * Plugin- and player-aware abstract menu base.
 *
 * <p>Extends {@link Menu} with an explicit reference to the owning plugin and
 * the viewing player. Subclasses implement {@link #populate()} instead of
 * {@link Menu#build()}.</p>
 */
public abstract class AbstractMenu extends Menu {

    protected final SkyblockPlugin plugin;
    protected final Player player;

    protected AbstractMenu(SkyblockPlugin plugin, Player player, String title, int size) {
        super(title, size / 9);
        this.plugin = plugin;
        this.player = player;
    }

    protected abstract void populate();

    @Override
    protected final void build() {
        populate();
    }
}
