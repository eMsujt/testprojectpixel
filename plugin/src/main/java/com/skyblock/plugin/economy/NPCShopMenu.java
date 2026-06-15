package com.skyblock.plugin.economy;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * A concrete 54-slot §6Shop chest GUI backed by a {@link ShopManager.Shop}.
 *
 * <p>Delegates all rendering and purchase logic to {@link ShopMenu}, using the
 * shop's own title and entry list.</p>
 */
@Deprecated
public class NPCShopMenu extends ShopMenu {

    /**
     * Creates a shop GUI for the given NPC shop definition.
     *
     * @param shop the shop to display
     */
    public NPCShopMenu(ShopManager.Shop shop) {
        super("§6" + shop.title(), shop.entries());
    }
}
