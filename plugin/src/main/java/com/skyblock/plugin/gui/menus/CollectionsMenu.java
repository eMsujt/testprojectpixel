package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The Collections hub menu.
 *
 * <p>A 54-slot (6-row) menu presenting one icon per collection category. Clicking
 * a category icon tells the player which category they selected; per-category
 * collection screens are opened by the dedicated sub-menus.</p>
 */
public class CollectionsMenu extends Menu {

    /** A collection category: its display name and representative icon. */
    private enum Category {
        FARMING("Farming", Material.WHEAT),
        MINING("Mining", Material.COBBLESTONE),
        COMBAT("Combat", Material.IRON_SWORD),
        FORAGING("Foraging", Material.OAK_LOG),
        FISHING("Fishing", Material.COD);

        private final String displayName;
        private final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Centred slots in the middle row, one per category. */
    private static final int[] SLOTS = {20, 21, 22, 23, 24};

    public CollectionsMenu() {
        super("Collections", 6);
    }

    @Override
    protected void build() {
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            setItem(SLOTS[i], new ItemBuilder(category.icon)
                            .displayName("§a" + category.displayName)
                            .lore("§7Click to view your " + category.displayName.toLowerCase() + " collections.")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§aOpening " + category.displayName + " collections..."));
        }
    }
}
