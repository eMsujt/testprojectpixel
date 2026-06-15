package com.skyblock.plugin.menu;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.CraftingMenu} instead.
 */
@Deprecated
public class CraftingTableMenu extends Menu {

    private final com.skyblock.core.menu.CraftingMenu delegate;

    public CraftingTableMenu() {
        super("§fCrafting Table", 5);
        this.delegate = new com.skyblock.core.menu.CraftingMenu();
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        delegate.open(player);
    }
}
