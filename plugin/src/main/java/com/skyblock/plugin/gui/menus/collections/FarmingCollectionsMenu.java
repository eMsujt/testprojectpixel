package com.skyblock.plugin.gui.menus.collections;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The Farming collections menu.
 *
 * <p>A 54-slot (6-row) menu listing one icon per farming collection. Clicking a
 * collection icon tells the player which collection they selected.</p>
 */
public class FarmingCollectionsMenu extends Menu {

    /** A farming collection: its display name and representative icon. */
    private enum Collection {
        WHEAT("Wheat", Material.WHEAT),
        CARROT("Carrot", Material.CARROT),
        POTATO("Potato", Material.POTATO),
        PUMPKIN("Pumpkin", Material.PUMPKIN),
        MELON("Melon", Material.MELON_SLICE),
        MUSHROOM("Mushroom", Material.RED_MUSHROOM),
        COCOA_BEANS("Cocoa Beans", Material.COCOA_BEANS),
        CACTUS("Cactus", Material.CACTUS),
        SUGAR_CANE("Sugar Cane", Material.SUGAR_CANE),
        NETHER_WART("Nether Wart", Material.NETHER_WART);

        private final String displayName;
        private final Material icon;

        Collection(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Centred slots across the two middle rows, one per collection. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 28, 29, 30, 31, 32};

    public FarmingCollectionsMenu() {
        super("Farming Collections", 6);
    }

    @Override
    protected void build() {
        Collection[] collections = Collection.values();
        for (int i = 0; i < collections.length; i++) {
            Collection collection = collections[i];
            setItem(SLOTS[i], new ItemBuilder(collection.icon)
                            .displayName("§a" + collection.displayName)
                            .lore("§7Click to view your " + collection.displayName.toLowerCase() + " collection.")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§aOpening " + collection.displayName + " collection..."));
        }
    }
}
