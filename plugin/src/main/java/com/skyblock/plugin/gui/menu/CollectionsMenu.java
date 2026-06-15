package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The Collections hub menu.
 *
 * <p>A 54-slot (6-row) menu with a gray glass-pane border. Each Hypixel
 * collection category is laid out across a centred grid as its representative
 * icon that, when clicked, refreshes the menu, matching Hypixel's layout.</p>
 */
public class CollectionsMenu extends Menu {

    /** A Hypixel collection category, its colour, wool icon and content slot. */
    private enum Category {
        FARMING("Farming", "§a", Material.LIME_WOOL, 10),
        MINING("Mining", "§7", Material.LIGHT_GRAY_WOOL, 19),
        COMBAT("Combat", "§c", Material.RED_WOOL, 28),
        FORAGING("Foraging", "§2", Material.GREEN_WOOL, 37),
        FISHING("Fishing", "§9", Material.BLUE_WOOL, 46);

        private final String displayName;
        private final String color;
        private final Material icon;
        private final int slot;

        Category(String displayName, String color, Material icon, int slot) {
            this.displayName = displayName;
            this.color = color;
            this.icon = icon;
            this.slot = slot;
        }
    }

    public CollectionsMenu() {
        super("§eCollections", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        for (Category category : Category.values()) {
            setItem(category.slot, new ItemBuilder(category.icon)
                            .displayName(category.color + category.displayName + " Collections")
                            .lore(
                                    "§7View your " + category.displayName.toLowerCase() + " collections.",
                                    "§eClick to view!")
                            .build(),
                    event -> open((Player) event.getWhoClicked()));
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
