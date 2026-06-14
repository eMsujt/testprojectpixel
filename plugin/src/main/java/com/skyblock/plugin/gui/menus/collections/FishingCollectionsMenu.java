package com.skyblock.plugin.gui.menus.collections;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The Fishing collections menu.
 *
 * <p>A 54-slot (6-row) menu listing one icon per fishing collection. Clicking a
 * collection icon tells the player which collection they selected.</p>
 */
public class FishingCollectionsMenu extends Menu {

    /** A fishing collection: its display name and representative icon. */
    private enum Collection {
        RAW_FISH("Raw Fish", Material.COD),
        RAW_SALMON("Raw Salmon", Material.SALMON),
        CLOWNFISH("Clownfish", Material.TROPICAL_FISH),
        PUFFERFISH("Pufferfish", Material.PUFFERFISH),
        PRISMARINE_SHARD("Prismarine Shard", Material.PRISMARINE_SHARD),
        PRISMARINE_CRYSTALS("Prismarine Crystals", Material.PRISMARINE_CRYSTALS),
        CLAY("Clay", Material.CLAY_BALL),
        LILY_PAD("Lily Pad", Material.LILY_PAD),
        INK_SAC("Ink Sac", Material.INK_SAC),
        SPONGE("Sponge", Material.SPONGE);

        private final String displayName;
        private final Material icon;

        Collection(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Slots across the two playable rows, one per collection. */
    private static final int[] SLOTS = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23};

    public FishingCollectionsMenu() {
        super("Fishing Collections", 6);
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
