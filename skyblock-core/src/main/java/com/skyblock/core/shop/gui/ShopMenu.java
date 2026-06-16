package com.skyblock.core.shop.gui;

import com.skyblock.core.menu.ShopMenu;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 */
@Deprecated
public class ShopMenu extends com.skyblock.core.menu.ShopMenu {

    public ShopMenu(String title, List<ShopItem> items) {
        super(title, items);
    }
}
