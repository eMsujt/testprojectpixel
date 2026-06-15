package com.skyblock.plugin.menu;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.QuestsMenu} instead.
 */
@Deprecated
public final class QuestsMenu extends Menu {

    /** @deprecated Use {@link com.skyblock.core.menu.QuestsMenu} instead. */
    @Deprecated
    public QuestsMenu() {
        super("§eQuests & Objectives", 6);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.QuestsMenu(player).open(player);
    }
}
