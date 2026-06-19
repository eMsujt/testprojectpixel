package com.skyblock.core.menu;

import com.skyblock.core.manager.BazaarManager.BazaarProduct;
import com.skyblock.core.util.SkyblockUtil.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GUI menu opened by {@code /bazaar}. Renders the bazaar product categories as
 * selector items (wheat/iron pickaxe/diamond sword/oak sapling/fishing rod/book),
 * showing for each the number of tradable products it groups.
 */
public final class BazaarMenu extends Menu {

    /** First slot of the centered category row; the categories occupy {@code FIRST_CATEGORY_SLOT .. +n-1}. */
    static final int FIRST_CATEGORY_SLOT = 20;

    /** Ordered category → icon mapping; order drives the slot layout. */
    private static final Map<String, Material> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put("FARMING",  Material.WHEAT);
        CATEGORIES.put("MINING",   Material.IRON_PICKAXE);
        CATEGORIES.put("COMBAT",   Material.DIAMOND_SWORD);
        CATEGORIES.put("FORAGING", Material.OAK_SAPLING);
        CATEGORIES.put("FISHING",  Material.FISHING_ROD);
        CATEGORIES.put("MISC",     Material.BOOK);
    }

    public BazaarMenu(Player player) {
        super("§eBazaar", 6);
    }

    @Override
    protected void build() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++) setItem(slot, pane);
        for (int slot = 45; slot < 54; slot++) setItem(slot, pane);

        int index = 0;
        for (Map.Entry<String, Material> entry : CATEGORIES.entrySet()) {
            String category = entry.getKey();
            int products = 0;
            for (BazaarProduct p : BazaarProduct.values()) {
                if (p.getCategory().equals(category)) products++;
            }

            setItem(FIRST_CATEGORY_SLOT + index, new ItemBuilder(entry.getValue())
                    .displayName("§a" + displayName(category))
                    .lore(
                            "§7Products: §e" + products,
                            "",
                            "§eClick to browse")
                    .build());
            index++;
        }
    }

    /** Title-cases a category id, e.g. {@code "FARMING"} → {@code "Farming"}. */
    private static String displayName(String category) {
        return category.charAt(0) + category.substring(1).toLowerCase();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
