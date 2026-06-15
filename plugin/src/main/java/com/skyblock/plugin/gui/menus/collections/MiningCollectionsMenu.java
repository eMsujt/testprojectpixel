package com.skyblock.plugin.gui.menus.collections;

import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;

/**
 * The Mining collections menu.
 *
 * <p>A 54-slot (6-row) menu listing one icon per mining collection. Clicking a
 * collection icon tells the player which collection they selected.</p>
 */
public class MiningCollectionsMenu extends Menu {

    /** A mining collection: its display name and representative icon. */
    private enum Collection {
        COBBLESTONE("Cobblestone", Material.COBBLESTONE),
        COAL("Coal", Material.COAL),
        IRON("Iron", Material.IRON_ORE),
        GOLD("Gold", Material.GOLD_ORE),
        DIAMOND("Diamond", Material.DIAMOND),
        LAPIS_LAZULI("Lapis Lazuli", Material.LAPIS_LAZULI),
        EMERALD("Emerald", Material.EMERALD),
        REDSTONE("Redstone", Material.REDSTONE),
        QUARTZ("Nether Quartz", Material.QUARTZ),
        OBSIDIAN("Obsidian", Material.OBSIDIAN);

        private final String displayName;
        private final Material icon;

        Collection(String displayName, Material icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
    }

    /** Centred slots across the two middle rows, one per collection. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 28, 29, 30, 31, 32};

    public MiningCollectionsMenu() {
        super("Mining Collections", 6);
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
