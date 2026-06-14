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

    /** A Hypixel collection category and its representative icon. */
    private enum Category {
        FARMING("Farming", Material.GOLDEN_HOE),
        MINING("Mining", Material.STONE_PICKAXE),
        COMBAT("Combat", Material.STONE_SWORD),
        FORAGING("Foraging", Material.JUNGLE_SAPLING),
        FISHING("Fishing", Material.FISHING_ROD),
        RIFT("Rift", Material.MYCELIUM);

        private final String displayName;
        private final Material icon;

        Category(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** The centred content slots, one per category. */
    private static final int[] SLOTS = {20, 21, 22, 23, 24, 30};

    public CollectionsMenu() {
        super("§2Collections", 6);
    }

    @Override
    protected void build() {
        fillBorder();

        Category[] values = Category.values();
        for (int i = 0; i < values.length; i++) {
            Category category = values[i];
            setItem(SLOTS[i], new ItemBuilder(category.icon)
                            .displayName("§a" + category.displayName + " Collections")
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
