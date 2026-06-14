package com.skyblock.plugin.economy;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Bazaar hub menu.
 *
 * <p>A 54-slot (6-row) menu presenting one icon per Bazaar category. Clicking a
 * category icon opens the corresponding {@link BazaarCategoryMenu}, listing the
 * items in that category alongside their current buy and sell prices.</p>
 */
public class BazaarMenu extends Menu {

    /** A Bazaar category: its display name, icon and the items it contains. */
    private enum Category {
        FARMING("Farming Ingredients", Material.WHEAT, List.of("WHEAT", "CARROT", "POTATO", "PUMPKIN", "MELON")),
        MINING("Mining", Material.COBBLESTONE, List.of("COAL", "IRON_INGOT", "GOLD_INGOT", "DIAMOND", "EMERALD")),
        COMBAT("Combat", Material.IRON_SWORD, List.of("ROTTEN_FLESH", "BONE", "STRING", "GUNPOWDER", "ENDER_PEARL")),
        WOODS_AND_FISHES("Woods and Fishes", Material.OAK_LOG, List.of("OAK_LOG", "BIRCH_LOG", "COD", "SALMON")),
        ODDS_AND_ENDS("Odds and Ends", Material.REDSTONE, List.of("REDSTONE", "LAPIS_LAZULI", "QUARTZ"));

        private final String displayName;
        private final Material icon;
        private final List<String> items;

        Category(String displayName, Material icon, List<String> items) {
            this.displayName = displayName;
            this.icon = icon;
            this.items = items;
        }
    }

    /** Centred slots in the middle row, one per category. */
    private static final int[] SLOTS = {20, 21, 22, 23, 24};

    /** Centred top-row slot holding the menu header. */
    private static final int HEADER_SLOT = 4;

    public BazaarMenu() {
        super("§6Bazaar", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        setItem(HEADER_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bazaar")
                .lore("§7Buy and sell items with other players.")
                .build());

        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            Category category = categories[i];
            setItem(SLOTS[i], new ItemBuilder(category.icon)
                            .displayName("§6" + category.displayName)
                            .lore("§7Click to browse " + category.displayName.toLowerCase() + " items.")
                            .build(),
                    event -> new BazaarCategoryMenu(category.displayName, category.items)
                            .open((Player) event.getWhoClicked()));
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }
}
