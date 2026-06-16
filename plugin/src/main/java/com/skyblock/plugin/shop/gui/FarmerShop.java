package com.skyblock.plugin.shop.gui;

import com.skyblock.core.menu.ShopMenu;
import org.bukkit.Material;

import java.util.List;

/**
 * The Farmer NPC shop: sells basic farming seeds and produce.
 */
public class FarmerShop extends ShopMenu {

    public FarmerShop() {
        super("§6Farmer", List.of(
                new ShopItem(Material.WHEAT_SEEDS, 6),
                new ShopItem(Material.WHEAT, 7),
                new ShopItem(Material.CARROT, 7),
                new ShopItem(Material.POTATO, 7),
                new ShopItem(Material.PUMPKIN_SEEDS, 8),
                new ShopItem(Material.MELON_SEEDS, 8),
                new ShopItem(Material.SUGAR_CANE, 8)
        ));
    }
}
