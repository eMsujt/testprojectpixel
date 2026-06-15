package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.economy.NpcShop;
import com.skyblock.plugin.economy.NpcShopItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bridges {@link NpcShop} (from NpcShopManager) to the generic {@link NpcShopMenu}.
 */
public class NpcShopGui extends NpcShopMenu {

    public NpcShopGui(NpcShop shop) {
        super(shop.displayName(), convert(shop.items()));
    }

    private static List<NpcShopMenu.ShopItem> convert(List<NpcShopItem> items) {
        return items.stream()
                .map(item -> new NpcShopMenu.ShopItem(
                        item.materialName(),
                        formatName(item.materialName()),
                        item.buyPrice()))
                .collect(Collectors.toList());
    }

    private static String formatName(String materialName) {
        String[] words = materialName.replace('_', ' ').split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return "§f" + sb;
    }
}
