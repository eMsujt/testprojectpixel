package com.skyblock.plugin.gui;

/**
 * @deprecated Use {@link com.skyblock.core.menu.Menu} instead.
 */
@Deprecated
public abstract class Menu extends com.skyblock.core.menu.Menu {
    protected Menu(String title, int rows) {
        super(title, rows);
    }
}
