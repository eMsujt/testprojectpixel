package com.skyblock.plugin.menus;

import com.skyblock.plugin.gui.Menu;
import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.AuctionHouseMenu} instead.
 */
@Deprecated
public class AuctionHouseMenu extends Menu {

    /** @deprecated Use {@link com.skyblock.core.menu.AuctionHouseMenu} instead. */
    @Deprecated
    public AuctionHouseMenu() {
        super("§6Auction House", 6);
    }

    @Override
    protected void build() {}

    @Override
    public void open(Player player) {
        new com.skyblock.core.menu.AuctionHouseMenu().open(player);
    }
}
