package com.skyblock.plugin.shops;

import com.skyblock.plugin.menus.NpcShopMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * The Farmer NPC shop: sells basic farming seeds and produce.
 *
 * <p>Stock is laid out left-to-right starting at slot 0 via
 * {@link #addStockItem(int, ItemStack, long)}, so each item can be purchased
 * with coins from the clicking player's purse.</p>
 */
public class FarmerShop extends NpcShopMenu {

    public FarmerShop() {
        super("§6Farmer");
    }

    @Override
    protected void build() {
        addStockItem(0, new ItemStack(Material.WHEAT_SEEDS), 6);
        addStockItem(1, new ItemStack(Material.WHEAT), 7);
        addStockItem(2, new ItemStack(Material.CARROT), 7);
        addStockItem(3, new ItemStack(Material.POTATO), 7);
        addStockItem(4, new ItemStack(Material.PUMPKIN_SEEDS), 8);
        addStockItem(5, new ItemStack(Material.MELON_SEEDS), 8);
        addStockItem(6, new ItemStack(Material.SUGAR_CANE), 8);
    }
}
