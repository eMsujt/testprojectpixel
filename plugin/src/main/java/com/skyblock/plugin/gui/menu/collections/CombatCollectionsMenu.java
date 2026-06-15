package com.skyblock.plugin.gui.menu.collections;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.core.menu.Menu;
import org.bukkit.Material;

/**
 * The Combat collections menu.
 *
 * <p>A 54-slot (6-row) menu listing one icon per combat collection. Clicking a
 * collection icon tells the player which collection they selected.</p>
 */
public class CombatCollectionsMenu extends Menu {

    /** A combat collection: its display name and representative icon. */
    private enum Collection {
        ROTTEN_FLESH("Rotten Flesh", Material.ROTTEN_FLESH),
        BONE("Bone", Material.BONE),
        STRING("String", Material.STRING),
        SPIDER_EYE("Spider Eye", Material.SPIDER_EYE),
        GUNPOWDER("Gunpowder", Material.GUNPOWDER),
        ENDER_PEARL("Ender Pearl", Material.ENDER_PEARL),
        SLIME_BALL("Slime Ball", Material.SLIME_BALL),
        BLAZE_ROD("Blaze Rod", Material.BLAZE_ROD);

        private final String displayName;
        private final Material icon;

        Collection(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Centred slots across the two middle rows, one per collection. */
    private static final int[] SLOTS = {20, 21, 22, 23, 29, 30, 31, 32};

    public CombatCollectionsMenu() {
        super("Combat Collections", 6);
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
