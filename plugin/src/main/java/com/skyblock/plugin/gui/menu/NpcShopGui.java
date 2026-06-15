package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.economy.NpcShopManager.NpcShop;
import com.skyblock.plugin.economy.NpcShopManager.NpcShopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * Concrete 54-slot chest GUI for an {@link NpcShop} loaded by
 * {@link com.skyblock.plugin.economy.NpcShopManager}. Converts each
 * {@link NpcShopItem} into a {@link NpcShopMenu.ShopItem} and delegates all
 * rendering and click-handling to {@link NpcShopMenu}.
 */
@Deprecated
public class NpcShopGui extends NpcShopMenu {

    public NpcShopGui(NpcShop shop) {
        super(Objects.requireNonNull(shop, "shop").displayName(), toShopItems(shop));
    }

    private static List<ShopItem> toShopItems(NpcShop shop) {
        List<ShopItem> result = new ArrayList<>();
        for (NpcShopItem item : shop.items()) {
            result.add(new ShopItem(item.materialName(), formatName(item.materialName()), item.buyPrice()));
        }
        return result;
    }

    private static String formatName(String materialName) {
        StringBuilder sb = new StringBuilder();
        for (String word : materialName.split("_")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase(Locale.ROOT))
                  .append(' ');
            }
        }
        return "§f" + sb.toString().trim();
    }
}
