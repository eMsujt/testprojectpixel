package com.skyblock.plugin.economy;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.core.manager.BazaarManager;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * A per-category Bazaar listing.
 *
 * <p>A 54-slot (6-row) menu showing each item in a single Bazaar category, laid
 * out left-to-right from the top-left slot. Every entry displays the item's
 * current buy and sell prices, read from {@link BazaarManager}.</p>
 */
public class BazaarCategoryMenu extends Menu {

    private final List<String> items;

    /**
     * Creates a category listing.
     *
     * @param categoryName the category's display name (shown in the title)
     * @param items        the item names to list
     */
    public BazaarCategoryMenu(String categoryName, List<String> items) {
        super("§8Bazaar ➜ " + categoryName, 6);
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    @Override
    protected void build() {
        BazaarManager bazaar = BazaarManager.getInstance();
        for (int i = 0; i < items.size() && i < 54; i++) {
            String item = items.get(i);
            Material icon = materialFor(item);
            setItem(i, new ItemBuilder(icon)
                    .displayName("§a" + item)
                    .lore("§7Buy Price: §6" + bazaar.getBuyPrice(item) + " coins",
                            "§7Sell Price: §6" + bazaar.getSellPrice(item) + " coins")
                    .build());
        }
    }

    /** Resolves an item name to a representative material, defaulting to paper. */
    private static Material materialFor(String item) {
        Material material = Material.matchMaterial(item);
        return material != null ? material : Material.PAPER;
    }
}
