package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.CollectionsMenu} instead.
 */
@Deprecated
public class CollectionsMenu extends Menu {

    public CollectionsMenu() {
        super("§6Collections", 6);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.CollectionsMenu(player.getUniqueId()).open(player);
    }
}
