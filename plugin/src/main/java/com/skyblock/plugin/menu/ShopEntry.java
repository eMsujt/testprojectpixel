package com.skyblock.plugin.menu;

import org.bukkit.Material;

import java.util.Objects;

/**
 * A single purchasable entry in an {@link NpcShopMenu}.
 *
 * @param material the item shown in the menu (also what the player receives)
 * @param price    the coin cost to purchase
 */
public record ShopEntry(Material material, int price) {
    public ShopEntry {
        Objects.requireNonNull(material, "material");
    }
}
