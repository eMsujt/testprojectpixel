package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.CollectionsMenu} instead.
 */
@Deprecated
public class CollectionsMenu extends Menu {

    private final com.skyblock.core.menu.CollectionsMenu delegate;

    public CollectionsMenu(UUID playerId) {
        super("§6Collections", 6);
        this.delegate = new com.skyblock.core.menu.CollectionsMenu(playerId);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        delegate.open(player);
    }
}
