package com.skyblock.plugin.economy;

/**
 * A concrete 54-slot §6Shop chest GUI backed by a {@link ShopManager.Shop}.
 *
 * <p>Delegates all rendering and purchase logic to {@link ShopMenu}, using the
 * shop's own title and entry list.</p>
 */
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
