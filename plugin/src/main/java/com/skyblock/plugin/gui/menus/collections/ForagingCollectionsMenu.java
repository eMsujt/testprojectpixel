package com.skyblock.plugin.gui.menus.collections;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The Foraging collections menu.
 *
 * <p>A 54-slot (6-row) menu listing one icon per foraging collection. Clicking a
 * collection icon tells the player which collection they selected.</p>
 */
public class ForagingCollectionsMenu extends Menu {

    /** A foraging collection: its display name and representative icon. */
    private enum Collection {
        OAK("Oak", Material.OAK_LOG),
        BIRCH("Birch", Material.BIRCH_LOG),
        SPRUCE("Spruce", Material.SPRUCE_LOG),
        DARK_OAK("Dark Oak", Material.DARK_OAK_LOG),
        ACACIA("Acacia", Material.ACACIA_LOG),
        JUNGLE("Jungle", Material.JUNGLE_LOG);

        private final String displayName;
        private final Material icon;

        Collection(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Slots across the top playable row, one per collection. */
    private static final int[] SLOTS = {10, 11, 12, 13, 14, 15};

    public ForagingCollectionsMenu() {
        super("Foraging Collections", 6);
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
